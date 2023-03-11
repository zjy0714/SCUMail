package com.zwh.cart.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "guli.thread")
@Data
public class ThreadPoolConfigProperties {
    private Integer maxSize;
    private Integer coreSize;
    private Integer keepAliveTime;
}
