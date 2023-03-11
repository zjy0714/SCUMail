package com.zwh.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.rabbitmq.client.Channel;
import com.zwh.common.exception.NoStockException;
import com.zwh.common.to.mq.OrderTo;
import com.zwh.common.to.mq.StockDetailTo;
import com.zwh.common.to.mq.StockLockedTo;
import com.zwh.common.utils.R;
import com.zwh.ware.entity.WareOrderTaskDetailEntity;
import com.zwh.ware.entity.WareOrderTaskEntity;
import com.zwh.ware.feign.OrderFeignService;
import com.zwh.ware.feign.ProductFeignService;
import com.zwh.ware.service.WareOrderTaskDetailService;
import com.zwh.ware.service.WareOrderTaskService;
import com.zwh.ware.vo.*;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zwh.common.utils.PageUtils;
import com.zwh.common.utils.Query;

import com.zwh.ware.dao.WareSkuDao;
import com.zwh.ware.entity.WareSkuEntity;
import com.zwh.ware.service.WareSkuService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    private WareSkuDao wareSkuDao;
    
    @Autowired
    private ProductFeignService productFeignService;

    @Autowired
    private WareOrderTaskService wareOrderTaskService;

    @Autowired
    private WareOrderTaskDetailService wareOrderTaskDetailService;

    @Autowired
    private OrderFeignService orderFeignService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareSkuEntity> queryWrapper = new QueryWrapper<>();
        String skuId = (String) params.get("skuId");
        if(!StringUtils.isEmpty(skuId)){
            queryWrapper.eq("sku_id",skuId);
        }
        String wareId = (String) params.get("wareId");
        if(!StringUtils.isEmpty(wareId)){
            queryWrapper.eq("ware_id",wareId);
        }
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        //如果没有库存记录则为新增操作
        List<WareSkuEntity> wareSkuEntities = wareSkuDao.selectList(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));
        if(wareSkuEntities.isEmpty()){
            WareSkuEntity skuEntity = new WareSkuEntity();
            skuEntity.setSkuId(skuId);
            skuEntity.setWareId(wareId);
            skuEntity.setStock(skuNum);
            skuEntity.setStockLocked(0);
            //远程查询sku名字 如果失败，整个事务无需回滚
            try {
                R info = productFeignService.info(skuId);
                Map<String,Object> data = (Map<String, Object>) info.get("skuInfo");
                if(info.getCode() == 0){
                    skuEntity.setSkuName((String) data.get("skuName"));
                }
            }catch (Exception e){
                e.printStackTrace();
                log.error("远程查询skuName失败");
            }
            wareSkuDao.insert(skuEntity);
        }else {
            wareSkuDao.addStock(skuId,wareId,skuNum);
        }
    }

    @Override
    public List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds) {
        return skuIds.stream().map(skuId -> {
            SkuHasStockVo hasStockVo = new SkuHasStockVo();
            //查询当前sku的总库存量
            Long count = this.baseMapper.getSkuStock(skuId);
            hasStockVo.setSkuId(skuId);
            hasStockVo.setHasStock(count!= null && count>0);
            return hasStockVo;
        }).collect(Collectors.toList());
    }

    /**
     * 为订单锁定库存
     * @param vo
     * @return
     */
    @Transactional(rollbackFor = NoStockException.class)
    @Override
    public Boolean orderLockStock(WareSkuLockVo vo) {
        //保存库存工作单详情
        WareOrderTaskEntity orderTaskEntity = new WareOrderTaskEntity();
        orderTaskEntity.setOrderSn(vo.getOrderSn());
        wareOrderTaskService.save(orderTaskEntity);
        //找到每个商品在哪个仓库都有库存
        List<OrderItemVo> locks = vo.getLocks();
        List<SkuWareHasStock> collect = locks.stream().map(item -> {
            SkuWareHasStock stock = new SkuWareHasStock();
            Long skuId = item.getSkuId();
            stock.setSkuId(skuId);
            stock.setNum(item.getCount());
            //查询这个商品在哪里有库存
            List<Long> wareIds = this.wareSkuDao.listWareIdHasSkuStock(skuId);
            stock.setWareId(wareIds);
            return stock;
        }).collect(Collectors.toList());
        //锁定库存
        for (SkuWareHasStock stock : collect) {
            boolean skuStocked = false;
            Long skuId = stock.getSkuId();
            List<Long> wareIds = stock.getWareId();
            if (CollectionUtils.isEmpty(wareIds)){
                throw new NoStockException(skuId);
            }
            for (Long wareId : wareIds) {
               Long count = wareSkuDao.lockSkuStock(skuId,wareId,stock.getNum());
               if (count == 1){
                    skuStocked=true;
                    //向MQ发送锁定库存成功的消息
                   WareOrderTaskDetailEntity detailEntity = new WareOrderTaskDetailEntity(null,skuId,"",stock.getNum(), orderTaskEntity.getId(), wareId,1);
                   wareOrderTaskDetailService.save(detailEntity);
                   StockLockedTo stockLockedTo = new StockLockedTo();
                   stockLockedTo.setId(orderTaskEntity.getId());
                   StockDetailTo detailTo = new StockDetailTo();
                   BeanUtils.copyProperties(detailEntity,detailTo);
                   stockLockedTo.setDetailTo(detailTo);
                   rabbitTemplate.convertAndSend("stock-event-exchange","stock-locked",stockLockedTo);
                   break;
               }
            }
            if (skuStocked){
                throw new NoStockException(skuId);
            }
        }
        return true;
    }

    /**
     * 解锁库存
     */
    private void unlockStock(Long skuId, Long wareId, Integer num, Long taskDetailId){
        //库存解锁
        wareSkuDao.unlockStock(skuId,wareId,num);
        //更新库存工作单的状态
        WareOrderTaskDetailEntity entity = new WareOrderTaskDetailEntity();
        entity.setId(taskDetailId);
        //变为已解锁
        entity.setLockStatus(2);
        wareOrderTaskDetailService.updateById(entity);
    }

    @Override
    public void unlockStock(StockLockedTo to) {
        //收到解锁库存的消息
        StockDetailTo detailTo = to.getDetailTo();
        Long detailId = detailTo.getId();
        //解锁
        //查询数据库关于这个订单的锁定库存消息
        WareOrderTaskDetailEntity taskDetail = wareOrderTaskDetailService.getById(detailId);
        if (taskDetail != null){
            //库存锁定成功
            Long id = to.getId();//库存工作单的id
            WareOrderTaskEntity taskEntity = wareOrderTaskService.getById(id);
            String orderSn = taskEntity.getOrderSn();//根据订单号查询订单状态
            R r = orderFeignService.getOrderStatus(orderSn);
            if (r.getCode() == 0){
                //订单数据返回成功
                OrderVo order = r.getData(new TypeReference<OrderVo>() {
                });
                if(order==null || order.getStatus() == 4){
                    //订单不存在或者订单被取消了，解锁库存
                    if (taskDetail.getLockStatus() == 1){
                        //当前库存工作单状态为已锁定但是未解锁才可以进行解锁
                        unlockStock(detailTo.getSkuId(), detailTo.getWareId(),detailTo.getSkuNum(),detailId);
                    }
                }
            }else {
                //消息拒绝后重新返回队列
                throw new RuntimeException("远程服务失败");
            }
        }
    }

    //防止订单服务卡顿，导致订单状态消息一直改不了，库存信息优先到期，只查询订单状态新建状态  导致订单卡顿，库存永远不能解锁
    @Transactional
    @Override
    public void unlockStock(OrderTo orderTo) {
        String orderSn = orderTo.getOrderSn();
        //查一下最新库存的状态，防止重复解锁库存
        WareOrderTaskEntity orderTaskEntity = wareOrderTaskService.getOrderTaskByOrderSn(orderSn);
        Long id = orderTaskEntity.getId();
        //按照工作单找到所有没有解锁的库存进行解锁
        List<WareOrderTaskDetailEntity> entities = wareOrderTaskDetailService.list(new QueryWrapper<WareOrderTaskDetailEntity>()
                .eq("task_id", id).eq("lock_status", 1));
        for (WareOrderTaskDetailEntity entity : entities) {
            unlockStock(entity.getSkuId(), entity.getWareId(), entity.getSkuNum(),entity.getId());
        }
    }
}
