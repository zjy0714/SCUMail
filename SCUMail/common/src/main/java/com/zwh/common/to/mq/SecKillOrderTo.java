package com.zwh.common.to.mq;

import com.zwh.common.vo.SkuInfoVo;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SecKillOrderTo {

    private String orderSn;
    private Long promotionSessionId;
    private Long skuId;
    private BigDecimal seckillPrice;
    private BigDecimal num;
    private Long memberId;
}
