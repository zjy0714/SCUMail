package com.zwh.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zwh.common.to.mq.SecKillOrderTo;
import com.zwh.common.utils.PageUtils;
import com.zwh.order.entity.OrderEntity;
import com.zwh.order.vo.*;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 订单
 *
 * @author zhangwenhao
 * @email z2388058390@163.com
 * @date 2022-09-27 16:32:24
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException;

    SubmitOrderResponseVo submitOrder(OrderSubmitVo vo);

    OrderEntity getOrderByOrderSn(String orderSn);

    void closeOrder(OrderEntity orderEntity);

    PayVo getOrderPay(String orderSn);

    PageUtils queryPageWithItem(Map<String, Object> params);

    String handlePayResult(PayAsyncVo vo);

    void createSecKillOrder(SecKillOrderTo order);
}

