package com.zwh.gulimember.controller;

import java.util.Arrays;
import java.util.Map;

import com.zwh.common.exception.BizCodeEnum;
import com.zwh.gulimember.exception.PhoneExistException;
import com.zwh.gulimember.exception.UsernameExitException;
import com.zwh.gulimember.feign.CouponFeignService;
import com.zwh.gulimember.vo.MemberLoginVo;
import com.zwh.gulimember.vo.RegisterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.zwh.gulimember.entity.MemberEntity;
import com.zwh.gulimember.service.MemberService;
import com.zwh.common.utils.PageUtils;
import com.zwh.common.utils.R;



/**
 * 会员
 *
 * @author zhangwenhao
 * @email z2388058390@163.com
 * @date 2022-09-27 16:21:54
 */
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

    @Autowired
    private CouponFeignService couponFeignService;

    @RequestMapping("/coupons")
    public R test(){
        MemberEntity member = new MemberEntity();
        member.setNickname("啊啊啊");
        R memberCoupons = couponFeignService.memberCoupons();
        return R.ok().put("member",member).put("coupons",memberCoupons.get("coupons"));
    }

    @PostMapping("/register")
    public R register(@RequestBody RegisterVo vo){
        try {
            memberService.register(vo);
        }catch (PhoneExistException e){
            return R.error(BizCodeEnum.PHONE_EXIST_EXCEPTION.getCode(), BizCodeEnum.PHONE_EXIST_EXCEPTION.getMsg());
        }catch (UsernameExitException e){
            return R.error(BizCodeEnum.USER_EXIST_EXCEPTION.getCode(), BizCodeEnum.USER_EXIST_EXCEPTION.getMsg());
        }
        return R.ok();
    }

    @PostMapping("/login")
    public R login(@RequestBody MemberLoginVo vo){
        MemberEntity entity = memberService.login(vo);
        if(entity != null){
            return R.ok();
        }else {
            return R.error(BizCodeEnum.LOGINACCT_PASSWORD_INVALID_EXCEPTION.getCode(),BizCodeEnum.LOGINACCT_PASSWORD_INVALID_EXCEPTION.getMsg());
        }
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("gulimember:member:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("gulimember:member:info")
    public R info(@PathVariable("id") Long id){
		MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("gulimember:member:save")
    public R save(@RequestBody MemberEntity member){
		memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("gulimember:member:update")
    public R update(@RequestBody MemberEntity member){
		memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("gulimember:member:delete")
    public R delete(@RequestBody Long[] ids){
		memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
