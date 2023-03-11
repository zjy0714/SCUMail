package com.zwh.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zwh.common.to.mq.OrderTo;
import com.zwh.common.to.mq.StockLockedTo;
import com.zwh.common.utils.PageUtils;
import com.zwh.ware.entity.WareSkuEntity;
import com.zwh.ware.vo.LockStockResultVo;
import com.zwh.ware.vo.SkuHasStockVo;
import com.zwh.ware.vo.WareSkuLockVo;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author zhangwenhao
 * @email z2388058390@163.com
 * @date 2022-09-27 17:17:11
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void addStock(Long skuId, Long wareId, Integer skuNum);

    List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds);

    Boolean orderLockStock(WareSkuLockVo vo);

    void unlockStock(StockLockedTo to);

    void unlockStock(OrderTo orderTo);
}

