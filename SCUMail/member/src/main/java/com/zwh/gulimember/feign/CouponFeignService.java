package com.zwh.gulimember.feign;

import com.zwh.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestMapping;

@Repository
@FeignClient(name = "coupon")
public interface CouponFeignService {
    @RequestMapping("/coupon/coupon/member/list")
    R memberCoupons();
}
