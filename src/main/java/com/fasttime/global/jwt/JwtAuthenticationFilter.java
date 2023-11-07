package com.fasttime.global.jwt;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.GenericFilter;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends GenericFilter {

    private final JwtProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = jwtTokenProvider.resolveToken((HttpServletRequest) request);

        if (token != null && jwtTokenProvider.validateToken(token)) {
            Authentication auth = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        chain.doFilter(request, response);
    }
}
//@Slf4j
//public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
//
//    private final AuthenticationManager manager;
//    private final JwtProvider provider;
//
//    public JwtAuthenticationFilter(AuthenticationManager manager, JwtProvider provider) {
//        super(manager);
//        this.provider = provider;
//        this.manager = manager;
//        setFilterProcessesUrl("/api/v1/login");
//    }
//
//    @Override
//    public Authentication attemptAuthentication(HttpServletRequest request
//        , HttpServletResponse response) throws AuthenticationException,RuntimeException {
//        ObjectMapper om = new ObjectMapper();
//        try {
//            LoginRequestDTO dto = om.readValue(request.getInputStream(), LoginRequestDTO.class);
//            UsernamePasswordAuthenticationToken authenticationToken =
//                new UsernamePasswordAuthenticationToken(dto.getEmail(),dto.getPassword());
//            log.info("[인증] attemptAuthentication 인증 시도");
//            setDetails(request,authenticationToken);
//            return manager.authenticate(authenticationToken);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Override
//    protected void successfulAuthentication(
//        HttpServletRequest request,
//        HttpServletResponse response,
//        FilterChain chain,
//        Authentication authResult
//    ) {
//        Member member = (Member) authResult.getPrincipal();
//        String token = provider.createToken(member.getEmail());
//        Cookie cookie = new Cookie(JwtProperties.COOKIE_NAME, token);
//        cookie.setMaxAge(JwtProperties.EXPIRATION_TIME);
//        cookie.setPath("/");
//        response.addCookie(cookie);
//        log.info("[인증] 인증 성공");
//    }
//
//    @Override
//    protected void unsuccessfulAuthentication(
//        HttpServletRequest request,
//        HttpServletResponse response,
//        AuthenticationException failed
//    ) throws IOException {
//        log.error("[인증] 인증 실패");
////        response.sendError(401,"로그인 실패");
//    }
//}
