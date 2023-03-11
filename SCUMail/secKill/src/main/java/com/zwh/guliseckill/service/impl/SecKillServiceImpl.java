package com.zwh.guliseckill.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.zwh.common.to.mq.SecKillOrderTo;
import com.zwh.common.utils.R;
import com.zwh.common.vo.MemberResponseVo;
import com.zwh.common.vo.SkuInfoVo;
import com.zwh.guliseckill.feign.CouponFeignService;
import com.zwh.guliseckill.feign.ProductFeignService;
import com.zwh.guliseckill.interceptor.LoginUserInterceptor;
import com.zwh.guliseckill.service.SecKillService;
import com.zwh.guliseckill.to.SecKillSkuRedisTo;
import com.zwh.guliseckill.vo.SecKillSessionWithSkus;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class SecKillServiceImpl implements SecKillService {

    private final String SESSIONS_CACHE_PREFIX = "secKil:sessions:";
    private final String SKUKILL_CACHE_PREFIX = "secKill:skus";
    private final String SKU_STOCK_SEMAPHORE = "secKill:stock";//后面接商品随机码

    @Autowired
    private CouponFeignService couponFeignService;

    @Autowired
    private ProductFeignService productFeignService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void uploadSecKillSkuLatest3Days() {
        //扫描最近三天需要参与秒杀的活动
        R r = couponFeignService.getLatest3DaySession();
        if (r.getCode() == 0){
            //上架商品
            List<SecKillSessionWithSkus> sessionWithSkus = r.getData(new TypeReference<List<SecKillSessionWithSkus>>() {
            });
            //缓存到redis中
            //缓存活动信息
            saveSessionInfos(sessionWithSkus);
            //缓存活动的关联商品信息
            saveSessionSkuInfos(sessionWithSkus);
        }
    }

    @Override
    public List<SecKillSkuRedisTo> getCurrentSecKillSkus() {
        //确定当前时间属于哪个秒杀场次
        long time = new Date().getTime();
        Set<String> keys = redisTemplate.keys(SESSIONS_CACHE_PREFIX + "*");
        if (keys != null) {
            for (String key : keys) {
                String replace = key.replace(SESSIONS_CACHE_PREFIX, "");
                String[] s = replace.split("_");
                long startTime = Long.parseLong(s[0]);
                long endTime = Long.parseLong(s[1]);
                if (time>=startTime && time<=endTime){
                    //获取当前场次需要的所有商品信息
                    List<String> range = redisTemplate.opsForList().range(key, -100, 100);
                    BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
                    List<String> list = null;
                    if (range != null) {
                        list = hashOps.multiGet(range);
                    }
                    if (list != null){
                        return list.stream().map(item -> JSON.parseObject(item, SecKillSkuRedisTo.class)).collect(Collectors.toList());
                    }
                    break;
                }
            }
        }
        return null;
    }

    @Override
    public SecKillSkuRedisTo getSkuSecKillInfo(Long skuId) {
        //找到所有要参与秒杀的商品的key
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
        Set<String> keys = hashOps.keys();
        if (keys != null && !keys.isEmpty()){
            String regx = "\\d_"+skuId;
            for (String key : keys) {
                if (Pattern.matches(regx,key)){
                    String json = hashOps.get(key);
                    SecKillSkuRedisTo skuRedisTo = JSON.parseObject(json, SecKillSkuRedisTo.class);
                    //不在当前场不返回随机码
                    if (skuRedisTo != null) {
                        long time = new Date().getTime();
                        if (time<=skuRedisTo.getStartTime() || time>=skuRedisTo.getEndTime()){
                            skuRedisTo.setRandomCode(null);
                        }
                    }
                    return skuRedisTo;
                }
            }
        }
        return null;
    }

    @Override
    public String kill(String killId, String key, Integer num) throws InterruptedException {
        MemberResponseVo memberResponseVo = LoginUserInterceptor.loginUser.get();
        //获取当前秒杀商品的详细信息
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
        String json = hashOps.get(killId);
        if (StringUtils.isEmpty(json)){
            return null;
        }else {
            SecKillSkuRedisTo redisTo = JSON.parseObject(json, SecKillSkuRedisTo.class);
            //校验合法性
            long time = new Date().getTime();
            long ttl = redisTo.getEndTime() - time;
            //校验时间的合法性
            if (time>=redisTo.getStartTime() && time<=redisTo.getEndTime()){
                //校验随机码和商品id
                String randomCode = redisTo.getRandomCode();
                String id = redisTo.getPromotionSessionId() + "_" + redisTo.getSkuId();
                if (randomCode.equals(key) && id.equals(killId)){
                    //验证购物数量是否合理
                    if (num<=redisTo.getSeckillLimit().intValue()){
                        //验证这个用户是否购买过
                        String userKey = memberResponseVo.getId()+"_"+id;
                        Boolean absent = redisTemplate.opsForValue().setIfAbsent(userKey, num.toString(),ttl, TimeUnit.MILLISECONDS);
                        if (Boolean.TRUE.equals(absent)){
                            RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE + randomCode);
                            boolean tryAcquire = semaphore.tryAcquire(num, 100, TimeUnit.MILLISECONDS);
                            if (tryAcquire){
                                //秒杀成功，快速下单
                                String timeId = IdWorker.getTimeId();
                                SecKillOrderTo secKillOrderTo = new SecKillOrderTo();
                                secKillOrderTo.setOrderSn(timeId);
                                secKillOrderTo.setNum(new BigDecimal(num));
                                secKillOrderTo.setMemberId(memberResponseVo.getId());
                                secKillOrderTo.setSkuId(redisTo.getSkuId());
                                secKillOrderTo.setSeckillPrice(redisTo.getSeckillPrice());
                                secKillOrderTo.setPromotionSessionId(redisTo.getPromotionSessionId());
                                rabbitTemplate.convertAndSend("order-event-exchange","order.secKill.order",secKillOrderTo);
                                return timeId;
                            }else {
                                return null;
                            }
                        }else {
                            return null;
                        }
                    }
                }else {
                    return null;
                }
            }else {
                return null;
            }
        }
        return null;
    }

    private void saveSessionInfos(List<SecKillSessionWithSkus> sessions){
        sessions.forEach(session -> {
            long startTime = session.getStartTime().getTime();
            long endTime = session.getEndTime().getTime();
            String key = SESSIONS_CACHE_PREFIX + startTime+"_"+endTime;
            //缓存活动信息
            if (Boolean.FALSE.equals(redisTemplate.hasKey(key))){
                List<String> collect = session.getRelationSkus().stream().map(item -> item.getPromotionSessionId()+"_"+item.getSkuId().toString()).collect(Collectors.toList());
                redisTemplate.opsForList().leftPushAll(key,collect);
            }
        });
    }

    private void saveSessionSkuInfos(List<SecKillSessionWithSkus> sessions){
        sessions.forEach(session -> {
            //准备hash操作
            BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
            //商品的随机码
            String token = UUID.randomUUID().toString().replace("-", "");
            session.getRelationSkus().forEach(secKillSkuVo -> {
                if (Boolean.FALSE.equals(hashOps.hasKey(secKillSkuVo.getPromotionSessionId().toString()+"_"+secKillSkuVo.getSkuId().toString()))){
                    //缓存商品
                    SecKillSkuRedisTo skuRedisTo = new SecKillSkuRedisTo();
                    //sku的基本信息
                    R skuInfo = productFeignService.getSkuInfo(secKillSkuVo.getSkuId());
                    if (skuInfo.getCode() == 0){
                        SkuInfoVo skuInfoVo = skuInfo.getData(new TypeReference<SkuInfoVo>() {
                        });
                        skuRedisTo.setSkuInfoVo(skuInfoVo);
                    }
                    //sku的秒杀信息
                    BeanUtils.copyProperties(secKillSkuVo,skuRedisTo);
                    //设置当前商品的秒杀信息
                    skuRedisTo.setStartTime(session.getStartTime().getTime());
                    skuRedisTo.setEndTime(session.getEndTime().getTime());
                    skuRedisTo.setRandomCode(token);
                    String s = JSON.toJSONString(skuRedisTo);
                    hashOps.put(secKillSkuVo.getPromotionSessionId().toString()+"_"+secKillSkuVo.getSkuId().toString(),s);
                    RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE + token);
                    //商品可以秒杀的数量作为信号量
                    semaphore.trySetPermits(secKillSkuVo.getSeckillCount().intValue());
                }
            });
        });
    }
}
