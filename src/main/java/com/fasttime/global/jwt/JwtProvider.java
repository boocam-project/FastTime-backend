package com.fasttime.global.jwt;

import com.fasttime.domain.member.service.MemberSecurityService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtProvider {

    private final MemberSecurityService memberSecurityService;

    @Value("${spring.jwt.secret}")
    private String secretKey;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes(StandardCharsets.UTF_8));
        log.info("[init] JwtProvider: SecretKey 초기화 완료");
    }

    public String createToken(String email) {
        Claims claims = Jwts.claims().setSubject(email);
        Date now = new Date();
        log.info("[createToken] JwtProvider: token 생성");
        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(new Date(now.getTime() + JwtProperties.EXPIRATION_TIME))
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact();
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = memberSecurityService.loadUserByUsername(getEmail(token));
        log.info("[getAuthentication] JwtProvider:토큰 인증 정보 조회 완료: ", userDetails.getUsername());
        return new UsernamePasswordAuthenticationToken
            (userDetails, "", userDetails.getAuthorities());
    }

    public String getEmail(String token) {
        log.info("[getEmail] JwtProvider: 토큰 기반 회원 이메일 추출");
        String subject = Jwts.parser().setSigningKey(secretKey)
            .parseClaimsJws(token.replace(JwtProperties.TOKEN_PREFIX,"")).getBody()
            .getSubject();
        System.out.println("subject: "+subject);
        return subject;
    }

    public String resolveToken(HttpServletRequest request) {
        log.info("[resolveToken] JwtProvider: HTTP 헤더에서 Token 값 추출");
        return request.getHeader(JwtProperties.COOKIE_NAME);
    }

    public boolean validateToken(String token) {
        log.info("[validateToken] 토큰 유효 체크 시작 ");
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey)
                .parseClaimsJws(token.replace(JwtProperties.TOKEN_PREFIX,""));
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            log.info("[validateToken] 토큰 유효 체크 예외 발생");
            return false;
        }
    }


}
