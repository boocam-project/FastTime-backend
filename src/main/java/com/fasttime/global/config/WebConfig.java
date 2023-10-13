package com.fasttime.global.config;

import com.fasttime.global.interceptor.AdminCheckInterceptor;
import com.fasttime.global.interceptor.LoginCheckInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Profile({"local, prod"})
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AdminCheckInterceptor())
            .order(1)
            .addPathPatterns("/api/v1/admin");
        registry.addInterceptor(new LoginCheckInterceptor())
            .order(2)
            .addPathPatterns("/api/v1/comment", "/api/v1/my-page/**", "/api/v1/post",
                "/api/v1/retouch-member", "/api/v1/logout", "/api/v1/delete","/api/v1/RePassword");

    }
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOriginPatterns("*").allowedMethods("GET", "POST", "PUT", "DELETE","OPTIONS");
    }

}
