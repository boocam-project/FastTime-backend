package com.fasttime.global.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

//Admin 권한이 필요한 URL 설정
@Slf4j
public class AdminCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);

        if (session != null && session.getAttribute("ADMIN") == null) {
            log.error("Admin Authorized Fail! / session.getAttribute(\"ADMIN\") info : {}", session.getAttribute("ADMIN"));
            response.sendError(403,"Admin 권한이 없습니다.");
            return false;
        }

        return true;
    }
}
