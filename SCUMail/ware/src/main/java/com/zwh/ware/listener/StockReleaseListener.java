package com.zwh.ware.listener;

import com.alibaba.fastjson.TypeReference;
import com.rabbitmq.client.Channel;
import com.zwh.common.to.mq.OrderTo;
import com.zwh.common.to.mq.StockDetailTo;
import com.zwh.common.to.mq.StockLockedTo;
import com.zwh.common.utils.R;
import com.zwh.ware.dao.WareSkuDao;
import com.zwh.ware.entity.WareOrderTaskDetailEntity;
import com.zwh.ware.entity.WareOrderTaskEntity;
import com.zwh.ware.feign.OrderFeignService;
import com.zwh.ware.service.WareOrderTaskDetailService;
import com.zwh.ware.service.WareOrderTaskService;
import com.zwh.ware.service.WareSkuService;
import com.zwh.ware.vo.OrderVo;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@RabbitListener(queues = "stock.release.stock.queue")
@Service
public class StockReleaseListener {

    @Autowired
    private WareSkuService wareSkuService;

    /**
     * 库存自动解锁
     * @param to
     * @param message
     */
    @RabbitHandler
    public void handleStockLockedRelease(StockLockedTo to, Message message, Channel channel) throws IOException {
        try {
            wareSkuService.unlockStock(to);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        }catch (Exception e){
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }
    }

    @RabbitHandler
    public void handleOrderCloseRelease(OrderTo orderTo, Message message, Channel channel) throws IOException {
        System.out.println("订单关闭准备解锁库存");
        try {
            wareSkuService.unlockStock(orderTo);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        }catch (Exception e){
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }
    }
}
