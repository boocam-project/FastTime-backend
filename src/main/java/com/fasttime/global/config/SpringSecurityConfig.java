package com.fasttime.global.config;

import lombok.RequiredArgsConstructor;


import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;


/**
 * Security 설정 Config
 */

//    private final JwtProvider jwtProvider;
//    private final AccessDeniedHandlerImpl accessDeniedHandler;

public class SpringSecurityConfig {
    //    private final JwtProvider jwtProvider;
//    private final AccessDeniedHandlerImpl accessDeniedHandler;

    @Bean
    SecurityFilterChain http(HttpSecurity http) throws Exception {
        http.httpBasic(AbstractHttpConfigurer::disable)
            .cors(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.httpBasic().disable() // basic authentication filter 비활성화 for 보안
//            .csrf().disable()
//            .formLogin().disable()
//            .rememberMe().disable()
//            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//            .and().authorizeRequests()
//            .antMatchers("/api/v1/join", "/api/v1/login", "/api/v1/admin/join", "/error")
//            .permitAll()
//            .antMatchers(HttpMethod.GET, "/api/v1/article").permitAll()
//            .antMatchers("/api/v1/admin/**").hasRole("ADMIN")
//            .anyRequest().authenticated()
//            .and()
//            .addFilterBefore(new JwtAuthenticationFilter(jwtProvider),
//                UsernamePasswordAuthenticationFilter.class)
//            .exceptionHandling()
//            .accessDeniedHandler(accessDeniedHandler);
//
//        http.logout().logoutRequestMatcher(new AntPathRequestMatcher("/api/v1/logout"));
//    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }


}