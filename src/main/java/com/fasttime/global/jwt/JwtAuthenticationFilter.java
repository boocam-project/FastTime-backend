package com.fasttime.global.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider provider;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain chain) throws IOException, ServletException {
        String token = resolveToken(request);
        if (token != null && !token.trim().isEmpty() && provider.validateToken(token)) {
            Authentication auth = provider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        chain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(JwtProperties.HEADER_STRING);
        log.info("[resolveToken] JwtProvider: HTTP 헤더에서 Token 값 추출");
        if (bearerToken != null && !bearerToken.trim().isEmpty() && bearerToken.startsWith(
            JwtProperties.TOKEN_PREFIX)) {
            return bearerToken.split(" ")[1].trim();
        }
        return null;
    }
}
