package com.fasttime.global.interceptor;

import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

//Admin 권한이 필요한 URL 설정
public class AdminCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
        Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        HttpSession session = request.getSession(false);

        if (session != null && session.getAttribute("ADMIN") == null) {
            response.sendError(403,"Admin 권한이 없습니다.");
            return false;
        }
        return true;
    }
}
