package com.fasttime.global.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider provider;
    private static final String EMPTY = "<EMPTY>";

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain chain) throws IOException, ServletException {
        String token = extractTokenFromHeader(request);
        if (token != null && !token.trim().isEmpty() && provider.validateToken(token)) {
            JwtPayload jwtPayload = provider.resolveToken(token);
            SecurityContextHolder.getContext()
                .setAuthentication(createAuthenticationToken(jwtPayload));
        }
        chain.doFilter(request, response);
    }

    private String extractTokenFromHeader(HttpServletRequest request) {
        String extractedHeaderValue = request.getHeader(HttpHeaders.AUTHORIZATION);

        log.debug("[resolveToken] JwtProvider: HTTP 헤더에서 Token 값 추출");

        if (extractedHeaderValue != null && !extractedHeaderValue.isBlank()
            && extractedHeaderValue.startsWith(JwtProperties.TOKEN_PREFIX)) {
            return extractedHeaderValue.substring(JwtProperties.TOKEN_PREFIX.length());
        }
        return null;
    }

    private Authentication createAuthenticationToken(JwtPayload jwtPayload) {
        Collection<? extends GrantedAuthority> authorities = Arrays.stream(
                jwtPayload.grantedAuthorities().split(","))
            .map(SimpleGrantedAuthority::new)
            .toList();

        UserDetails principal = new User(jwtPayload.name(), EMPTY, authorities);
        return UsernamePasswordAuthenticationToken.authenticated(principal, EMPTY, authorities);
    }
}
