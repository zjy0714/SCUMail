package com.zwh.guliseckill.controller;

import com.zwh.common.utils.R;
import com.zwh.guliseckill.service.SecKillService;
import com.zwh.guliseckill.to.SecKillSkuRedisTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SecKillController {

    @Autowired
    private SecKillService secKillService;

    /**
     * 返回当前商品可以参与秒杀的商品信息
     * @return
     */
    @GetMapping("/currentSecKillSkus")
    public R getCurrentSecKillSkus(){
        List<SecKillSkuRedisTo> vos = secKillService.getCurrentSecKillSkus();
        return R.ok().setData(vos);
    }

    @GetMapping("/sku/secKill/{skuId}")
    public R getSkuSecKillInfo(@PathVariable("skuId") Long skuId){
        SecKillSkuRedisTo to = secKillService.getSkuSecKillInfo(skuId);
        return R.ok().setData(to);
    }

    @GetMapping("/kill")
    public R secKill(@RequestParam("killId") String killId,
                     @RequestParam("key") String key,
                     @RequestParam("num") Integer num) throws InterruptedException {
        String orderSn = secKillService.kill(killId,key,num);
        return R.ok().setData(orderSn);
    }
}
