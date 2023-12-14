package com.fasttime.global.jwt;

public record JwtPayload(
    String name,
    String grantedAuthorities
) {

}
