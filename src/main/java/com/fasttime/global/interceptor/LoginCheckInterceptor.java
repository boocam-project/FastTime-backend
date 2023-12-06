package com.fasttime.global.interceptor;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

//로그인을 해야 되는 URL 설정
@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
        Object handler) throws Exception {
        if (isGetPost(request)) {
            return true;
        }
        HttpSession session = request.getSession(false);

        if (isSessionNull(session)) {
            log.error("Member Authorized Fail! / session is null");
            response.sendError(403,"로그인 후 이용가능합니다.");
            return false;
        }

        if (isNotLogin(session)) {
            log.error("Member Authorized Fail! / session.getAttribute(\"MEMBER\") info : {}", session.getAttribute("MEMBER"));
            response.sendError(403,"로그인 후 이용가능합니다.");
            return false;
        }
        return true;
    }

    private  boolean isGetPost(HttpServletRequest request) {
        boolean contains = request.getRequestURI().contains("/api/v1/post");
        boolean get = request.getMethod().equals("GET");
        if (contains && get){
                return true;
        }
        return false;
    }

    private  boolean isSessionNull(HttpSession session) {
        return session == null;
    }

    private  boolean isNotLogin(HttpSession session) {
        return (session.getAttribute("MEMBER") == null && session.getAttribute("ADMIN") == null);
    }
}

