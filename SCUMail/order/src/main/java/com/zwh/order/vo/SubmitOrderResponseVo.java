package com.zwh.order.vo;

import com.zwh.order.entity.OrderEntity;
import lombok.Data;

@Data
public class SubmitOrderResponseVo {
    private OrderEntity order;
    private Integer code;  //0成功   状态码
}
