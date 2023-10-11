package com.fasttime.global.interceptor;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

//로그인을 해야 되는 URL 설정
@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
        Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        HttpSession session = request.getSession(false);

        if (session == null || (session.getAttribute("MEMBER") == null
            && session.getAttribute("ADMIN") == null)) {
            response.sendError(403,"로그인 후 이용가능합니다.");
            return false;
        }
        return true;
    }
}

