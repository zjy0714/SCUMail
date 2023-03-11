package com.zwh.ware.vo;

import lombok.Data;

import java.util.List;

@Data
public class SkuWareHasStock {
    private Long skuId;
    private List<Long> wareId;
    //要锁定的件数
    private Integer num;
}
