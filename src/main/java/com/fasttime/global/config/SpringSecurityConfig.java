package com.fasttime.global.config;

import com.fasttime.domain.member.repository.MemberRepository;
import com.fasttime.global.jwt.JwtAuthenticationFilter;
import com.fasttime.global.jwt.JwtAuthorizationFilter;
import com.fasttime.global.jwt.JwtProvider;
import com.fasttime.global.util.ResponseDTO;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;


import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


/**
 * Security 설정 Config
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {
    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic().disable(); // basic authentication filter 비활성화 for 보안

        http.csrf().disable();

        http.rememberMe().disable();

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(
            new JwtAuthenticationFilter(authenticationManager(),jwtProvider),
            UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(
                new JwtAuthorizationFilter(memberRepository,jwtProvider),
                BasicAuthenticationFilter.class
            );

        http.authorizeRequests()
            .antMatchers("/api/v1/join","/api/v1/login","/api/v1/admin/join")
            .permitAll()
            .antMatchers(HttpMethod.GET,"/api/v1/article").permitAll()
            .antMatchers("/api/v1/RePassword").hasRole("MEMBER")
            .antMatchers("/api/v1/admin/**").hasRole("ADMIN")
            .anyRequest().authenticated();



        http.formLogin().disable();
//            .usernameParameter("email")
//            .passwordParameter("password")
//            .loginProcessingUrl("/api/v1/login")
//            .successHandler(
//                (request, response, authentication) -> ResponseEntity.status(HttpStatus.OK).body(
//                     ResponseDTO.res(HttpStatus.OK, "로그인이 완료되었습니다.")
//            ))
//            .failureHandler(
//                (request, response, exception) -> {
//                    System.out.println("exception : " + exception.getMessage());
////                    response.sendError(403, "로그인 불가");
//                }
//            )
//            .permitAll();

        http.logout().disable();
//            .logoutRequestMatcher(new AntPathRequestMatcher("/api/v1/logout"));
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }



}