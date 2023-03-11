package com.zwh.guliseckill.schedule;

import com.zwh.guliseckill.service.SecKillService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class SecKillSkuScheduled {

    @Autowired
    private SecKillService secKillService;

    @Autowired
    private RedissonClient redissonClient;

    private final String upload_lock = "secKill:upload:lock";

    //每天晚上三点上架近三天要秒杀的商品
    @Scheduled(cron = "0 0 3 * * ?")
    public void uploadSecKillSkuLatest3Days(){
        //重复上架问题无需处理
        log.info("上架要秒杀的商品");
        //加分布式锁
        RLock lock = redissonClient.getLock(upload_lock);
        lock.lock(10, TimeUnit.SECONDS);
        try {
            secKillService.uploadSecKillSkuLatest3Days();
        }finally {
            lock.unlock();
        }
    }
}
