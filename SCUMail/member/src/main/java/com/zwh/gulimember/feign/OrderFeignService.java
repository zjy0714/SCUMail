package com.zwh.gulimember.feign;

import com.zwh.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient("guli-order")
public interface OrderFeignService {
    @PostMapping("/order/order/listWithItem")
    R listWithItem(@RequestBody Map<String,Object> params);
}
