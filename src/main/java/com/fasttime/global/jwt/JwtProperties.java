package com.fasttime.global.jwt;

public class JwtProperties {
    public static final int ACCESS_EXPIRATION_TIME = 1000 * 60 * 30; // 30 minutes
    public static final int REFRESH_EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 7; // 1 week
    public static final String AUTHORITIES_KEY = "auth";
    public static final String HEADER_STRING = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
}
