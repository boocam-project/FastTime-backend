package com.fasttime.global.jwt;

public class JwtProperties {
    public static final int EXPIRATION_TIME = 1000 * 60 * 60 * 6; // 6 hour
    public static final String HEADER_STRING = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
}
