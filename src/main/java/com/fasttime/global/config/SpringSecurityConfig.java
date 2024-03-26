package com.fasttime.global.config;

import com.fasttime.global.jwt.JwtAccessDeniedHandler;
import com.fasttime.global.jwt.JwtAuthenticationEntryPoint;
import com.fasttime.global.jwt.JwtAuthenticationFilter;
import com.fasttime.global.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;


/**
 * Security 설정 Config
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SpringSecurityConfig {

    private final JwtProvider jwtProvider;
    private final CorsFilter corsFilter;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private static final String[] PERMIT_URL_ARRAY = {
        "/",
        "/error",
        "/docs/**",
        "/api/v1/members",
        "/api/v2/login",
        "/api/v2/refresh",
        "/api/v1/admin/join",
        "/api/live/**",
        "/api/dashboards/**",
        "/actuator/**",
        "/api/v2/activities/**",
        "/api/v2/competitions/**",
        "/api/v1/confirm",
        "/api/v1/verify/**",
    };
    private static final String[] GRAFANA_WHITE_LIST = {
        "/public/**",
        "/grafana/**",
        "/dashboard/**"
    };

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.httpBasic(AbstractHttpConfigurer::disable)
            .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(
                configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(requests -> requests
                .requestMatchers(PERMIT_URL_ARRAY).permitAll()
                .requestMatchers(GRAFANA_WHITE_LIST).permitAll()
                .requestMatchers(HttpMethod.GET, "api/v1/article", "api/v2/articles", "api/v2/reviews/**").permitAll()
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                .anyRequest().authenticated())
            .formLogin(AbstractHttpConfigurer::disable)
            .addFilter(corsFilter)
            .addFilterBefore(new JwtAuthenticationFilter(jwtProvider),
                UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(authenticationManager -> authenticationManager
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}