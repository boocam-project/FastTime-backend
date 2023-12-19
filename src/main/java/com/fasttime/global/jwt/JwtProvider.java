package com.fasttime.global.jwt;

import com.fasttime.domain.member.dto.response.TokenResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class JwtProvider implements InitializingBean {

    @Value("${spring.jwt.secret}")
    private String secretKey;

    private Key key;

    @Override
    public void afterPropertiesSet() throws Exception {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public TokenResponse createToken(JwtPayload payload) {
        long now = new Date().getTime();

        String accessToken = Jwts.builder()
            .setSubject(payload.name())
            .claim(JwtProperties.AUTHORITIES_KEY, payload.grantedAuthorities())
            .setExpiration(new Date(now + JwtProperties.ACCESS_EXPIRATION_TIME))
            .signWith(key)
            .compact();

        String refreshToken = Jwts.builder()
            .setExpiration(new Date(now + JwtProperties.REFRESH_EXPIRATION_TIME))
            .signWith(key)
            .compact();

        log.debug("[createToken] JwtProvider: token 생성");
        return TokenResponse.builder()
            .grantType(JwtProperties.TOKEN_PREFIX)
            .accessToken(accessToken)
            .accessTokenExpiresIn(new Date(now + JwtProperties.ACCESS_EXPIRATION_TIME).getTime())
            .refreshToken(refreshToken)
            .build();
    }

    public JwtPayload resolveToken(String token) {
        Claims claims = parseClaims(token);

        if (claims.get(JwtProperties.AUTHORITIES_KEY) == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        return new JwtPayload(claims.getSubject(), claims.get(JwtProperties.AUTHORITIES_KEY).toString());
    }

    public boolean validateToken(String token) {
        log.debug("[validateToken] 토큰 유효 체크 시작 ");
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.debug("잘못된 JWT 서명 입니다.");
        } catch (ExpiredJwtException e) {
            log.debug("만료된 JWT 토큰 입니다.");
        } catch (UnsupportedJwtException e) {
            log.debug("지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            log.debug("JWT 토큰이 잘못 되었습니다.");
        }
        return false;
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(accessToken)
                .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
