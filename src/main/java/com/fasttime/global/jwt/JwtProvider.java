package com.fasttime.global.jwt;

import com.fasttime.domain.member.dto.response.TokenResponseDto;
import com.fasttime.domain.member.service.MemberSecurityService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class JwtProvider implements InitializingBean {

    private final MemberSecurityService memberSecurityService;

    @Value("${spring.jwt.secret}")
    private String secretKey;

    private Key key;

    @Override
    public void afterPropertiesSet() throws Exception {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public TokenResponseDto createToken(Authentication authentication) {
        String claims = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));
        long now = new Date().getTime();
        Date accessTokenExpiresIn = new Date(now + JwtProperties.ACCESS_EXPIRATION_TIME);
//        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        String accessToken = Jwts.builder()
            .setSubject(authentication.getName())
            .claim(JwtProperties.AUTHORITIES_KEY, claims)
            .setExpiration(accessTokenExpiresIn)
            .signWith(SignatureAlgorithm.HS512, key)
            .compact();
        String refreshToken = Jwts.builder()
            .setExpiration(new Date(now + JwtProperties.REFRESH_EXPIRATION_TIME))
            .signWith(SignatureAlgorithm.HS512, key)
            .compact();
        log.info("[createToken] JwtProvider: token 생성");
        return TokenResponseDto.builder()
            .grantType(JwtProperties.TOKEN_PREFIX)
            .accessToken(accessToken)
            .accessTokenExpiresIn(accessTokenExpiresIn.getTime())
            .refreshToken(refreshToken)
            .build();
    }


    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);
        if (claims.get(JwtProperties.AUTHORITIES_KEY) == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }
        Collection<? extends GrantedAuthority> authorities = Arrays.stream(
                claims.get(JwtProperties.AUTHORITIES_KEY).toString().split(","))
            .map(SimpleGrantedAuthority::new)
            .toList();
        UserDetails principal = new User(claims.getSubject(), "", authorities);
        log.info("[getAuthentication] JwtProvider:토큰 인증 정보 조회 완료: ", principal.getUsername());
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }


    public boolean validateToken(String token) {
        log.info("[validateToken] 토큰 유효 체크 시작 ");
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
