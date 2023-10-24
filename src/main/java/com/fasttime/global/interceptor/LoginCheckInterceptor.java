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
        if (isGetPost(request)) {
            return true;
        }
        HttpSession session = request.getSession(false);

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

    private  boolean isNotLogin(HttpSession session) {
        return session == null || (session.getAttribute("MEMBER") == null
            && session.getAttribute("ADMIN") == null);
    }
}

