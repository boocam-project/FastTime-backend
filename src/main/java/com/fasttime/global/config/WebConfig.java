package com.fasttime.global.config;

import com.fasttime.global.interceptor.AdminCheckInterceptor;
import com.fasttime.global.interceptor.LoginCheckInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AdminCheckInterceptor())
            .order(1)
            .addPathPatterns("/v1/admin");
        registry.addInterceptor(new LoginCheckInterceptor())
            .order(2)
            .addPathPatterns("/api/v1/post", "/api/v1/comment",
                "v1/retouch-member", "v1/logout", "v1/delete");
    }
}
