package com.fasttime.global.config;

import com.fasttime.global.interceptor.AdminCheckInterceptor;
import com.fasttime.global.interceptor.LoginCheckInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Profile("!test")
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AdminCheckInterceptor())
            .order(1)
            .addPathPatterns("/api/v1/admin/**")
            .excludePathPatterns("/api/v1/admin/login")
            .excludePathPatterns("/api/v1/admin/join");
        registry.addInterceptor(new LoginCheckInterceptor())
            .order(2)
            .addPathPatterns("/api/v1/comment", "/api/v1/my-page/**", "/api/v1/post",
                "/api/v1/report/**", "/api/v1/record/**",
                "/api/v1/retouch-member", "/api/v1/logout", "/api/v1/delete","/api/v1/RePassword");

    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOriginPatterns("http://localhost:3000", "https://localhost:3000",
                "http://127.0.0.1:3000", "https://127.0.0.1:3000", "http://localhost:5173",
                "https://localhost:5173", "http://127.0.0.1:5173", "https://127.0.0.1:5173")
            .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
            .allowCredentials(true)
            .exposedHeaders("*");
    }

}
