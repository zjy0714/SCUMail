package com.zwh.guliproduct.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "guli.thread")
@Data
public class ThreadPoolConfigProperties {
    private Integer maxSize;
    private Integer coreSize;
    private Integer keepAliveTime;
}
