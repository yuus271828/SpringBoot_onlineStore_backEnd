package com.ohiji.security.jwt;


public class JWTConstants {
    public static final String SECRET_KEY = "Ohiji@test";
    public static final String EMAIL_VERIFIED_KEY = "%ohiji@email";
    public static final String EMAIL_RESET_PASSWORD_KEY = "%ohiji@resetPassword/";
    public static final long EXPIRATION_TIME = 900_000; // 15 minutes
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
}
