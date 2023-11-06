package com.fasttime.global.jwt;


import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.exception.UserNotFoundException;
import com.fasttime.domain.member.repository.MemberRepository;
import java.io.IOException;
import java.util.Arrays;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;

    public JwtAuthorizationFilter(MemberRepository memberRepository,JwtProvider jwtProvider) {
        this.memberRepository = memberRepository;
        this.jwtProvider = jwtProvider;
    }
    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain chain
    ) throws IOException, ServletException {
        String token = null;
        try {
            // cookie 에서 JWT token을 가져옵니다.
            token = Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(JwtProperties.COOKIE_NAME)).findFirst()
                .map(Cookie::getValue)
                .orElse(null);
        } catch (Exception ignored) {
//            response.sendError(403,"인가 실패");
        }
        if (token != null) {
            try {
                Authentication authentication = getEmailPasswordAuthenticationToken(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                Cookie cookie = new Cookie(JwtProperties.COOKIE_NAME, null);
                cookie.setMaxAge(0);
                response.addCookie(cookie);
//                response.sendError(403,"인가 실패");
            }
        }
        log.info("[인가] 인가 성공");
        chain.doFilter(request, response);
    }
    private Authentication getEmailPasswordAuthenticationToken(String token) {
        String email = jwtProvider.getEmail(token);
        if (email != null) {
            Member member = memberRepository.findByEmail(email)
                .orElseThrow(()-> new UserNotFoundException());
            return new UsernamePasswordAuthenticationToken(
                member, // principal
                null,
                member.getAuthorities()
            );
        }
        return null; // 유저가 없으면 NULL
    }
}
