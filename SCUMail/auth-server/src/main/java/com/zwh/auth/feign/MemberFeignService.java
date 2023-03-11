package com.zwh.auth.feign;

import com.zwh.auth.vo.RegisterVo;
import com.zwh.auth.vo.UserLoginVo;
import com.zwh.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("guli-member")
public interface MemberFeignService {
    @PostMapping("/member/member/register")
    R register(@RequestBody RegisterVo vo);

    @PostMapping("/member/member/login")
    R login(@RequestBody UserLoginVo vo);
}
