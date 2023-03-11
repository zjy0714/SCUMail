package com.zwh.guliproduct.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class MyRedissonConfig {

    /**
     * 所有对Redisson的
     * @return
     * @throws IOException
     */
    @Bean(destroyMethod = "shutdown")
    public RedissonClient redisson() throws IOException {
        Config config = new Config();
//        config.useClusterServers().addNodeAddress("redis://121.5.134.227:6379");
        config.useSingleServer().setAddress("redis://121.5.134.227:6379");
        return Redisson.create(config);
    }
}
