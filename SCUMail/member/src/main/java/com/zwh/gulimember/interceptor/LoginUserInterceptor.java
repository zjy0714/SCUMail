package com.zwh.gulimember.interceptor;

import com.zwh.common.constant.AuthServerConstant;
import com.zwh.common.vo.MemberResponseVo;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoginUserInterceptor implements HandlerInterceptor {

    public static ThreadLocal<MemberResponseVo> loginUser = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        boolean match = new AntPathMatcher().match("/member/memberreceiveaddress/**", uri);
        if (match){
            return true;
        }
        MemberResponseVo attribute = (MemberResponseVo) request.getSession().getAttribute(AuthServerConstant.LOGIN_USER);
        if (attribute != null){
            //已登陆
            loginUser.set(attribute);
            return true;
        }else {
            request.getSession().setAttribute("msg","请先登录");
            response.sendRedirect("http://auth.gulimall.com/login.html");
            return false;
        }
    }
}
