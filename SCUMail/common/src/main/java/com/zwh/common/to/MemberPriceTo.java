package com.zwh.common.to;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MemberPriceTo {
    private Long id;
    private String name;
    private BigDecimal price;
}
