package com.instantinsights.api.jwt.services;

import io.jsonwebtoken.Claims;

import java.security.Key;

public interface JwtService {
    String generateToken(String subject, Key key, Long expirationMillis);

    boolean validateToken(String jwt, Key key);

    Claims getClaims(String jwt, Key key);

    Key getKey(String secret);
}
