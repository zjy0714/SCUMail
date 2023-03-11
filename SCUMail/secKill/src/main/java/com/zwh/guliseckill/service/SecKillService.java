package com.zwh.guliseckill.service;

import com.zwh.guliseckill.to.SecKillSkuRedisTo;

import java.util.List;

public interface SecKillService {

    void uploadSecKillSkuLatest3Days();

    List<SecKillSkuRedisTo> getCurrentSecKillSkus();

    SecKillSkuRedisTo getSkuSecKillInfo(Long skuId);

    String kill(String killId, String key, Integer num) throws InterruptedException;
}
