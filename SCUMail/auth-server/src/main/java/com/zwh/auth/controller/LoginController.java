package com.zwh.auth.controller;

import com.alibaba.fastjson.TypeReference;
import com.zwh.auth.feign.MemberFeignService;
import com.zwh.auth.feign.ThirdPartyFeignService;
import com.zwh.auth.vo.RegisterVo;
import com.zwh.auth.vo.UserLoginVo;
import com.zwh.common.constant.AuthServerConstant;
import com.zwh.common.exception.BizCodeEnum;
import com.zwh.common.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Controller
public class LoginController {

    @Autowired
    private ThirdPartyFeignService thirdPartyFeignService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private MemberFeignService memberFeignService;

    @ResponseBody
    @GetMapping("/sms/sendcode")
    public R sendCode(@RequestParam("phone") String phone){
        String code0 = stringRedisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone);
        if(!StringUtils.isEmpty(code0)){
            long time = Long.parseLong(code0.split("_")[1]);
            if(System.currentTimeMillis()-time < 60000){
                //60秒内不能再次发送
                return R.error(BizCodeEnum.SMS_CODE_EXCEPTION.getCode(),BizCodeEnum.SMS_CODE_EXCEPTION.getMsg());
            }
        }
        //TODO 接口防刷

        String code = UUID.randomUUID().toString().substring(0, 5);
        String s = UUID.randomUUID().toString().substring(0, 5)+"_"+System.currentTimeMillis();
        stringRedisTemplate.opsForValue().set(AuthServerConstant.SMS_CODE_CACHE_PREFIX+phone,s,10, TimeUnit.MINUTES);
        thirdPartyFeignService.sendCode(phone,code);
        return R.ok();
    }

    /**
     * 注册请求
     * @return
     */
    @PostMapping("/register")
    public String register(@Valid RegisterVo vo, BindingResult bindingResult, RedirectAttributes redirectAttributes){
        if(bindingResult.hasErrors()){
            Map<String, String> errors = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            redirectAttributes.addFlashAttribute("errors",errors);
            //校验出错，返回到注册页
            return "redirect:/reg.html";
        }
        //调用远程服务进行注册
        //校验验证码
        String code = vo.getCode();
        String s = stringRedisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vo.getPhone());
        if(!StringUtils.isEmpty(s)){
            if(code.equals(s.split("_")[0])){
                //删除验证码
                stringRedisTemplate.delete(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vo.getPhone());
                //注册
                R register = memberFeignService.register(vo);
                if(register.getCode() == 0){
                    return "redirect:/login.html";
                }else {
                    Map<String,String> errors = new HashMap<>();
                    errors.put("msg",register.getData("msg",new TypeReference<String>(){}));
                    redirectAttributes.addFlashAttribute("errors",errors);
                    return "redirect:/reg.html";
                }
            }else {
                Map<String,String> errors = new HashMap<>();
                errors.put("code","验证码错误");
                redirectAttributes.addFlashAttribute("errors",errors);
                //校验出错，返回到注册页
                return "redirect:/reg.html";
            }
        }else {
            Map<String,String> errors = new HashMap<>();
            errors.put("code","验证码错误");
            redirectAttributes.addFlashAttribute("errors",errors);
            //校验出错，返回到注册页
            return "redirect:/reg.html";
        }
    }

    @PostMapping("/login")
    public String login(UserLoginVo vo,RedirectAttributes redirectAttributes){
        R login = memberFeignService.login(vo);
        if(login.getCode() == 0){
            return "redirect:index.html";
        }else {
            Map<String,String> errors = new HashMap<>();
            errors.put("msg",login.getData("msg",new TypeReference<String>(){}));
            redirectAttributes.addFlashAttribute("errors",errors);
            return "redirect:login.html";
        }
    }
}
