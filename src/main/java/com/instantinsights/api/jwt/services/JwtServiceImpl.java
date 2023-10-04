package com.instantinsights.api.jwt.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtServiceImpl implements JwtService {
    @Override
    public String generateToken(String subject, Key key, Long expirationMillis) {
        return Jwts.builder()
                   .setSubject(subject)
                   .setIssuedAt(new Date())
                   .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
                   .signWith(key, SignatureAlgorithm.HS256)
                   .compact();
    }

    @Override
    public boolean validateToken(String token, Key key) {
        return Jwts.parserBuilder()
                   .setSigningKey(key)
                   .build()
                   .parseClaimsJws(token)
                   .getBody()
                   .getExpiration()
                   .after(new Date());
    }

    public Claims getClaims(String token, Key key) {
        return Jwts.parserBuilder()
                   .setSigningKey(key)
                   .build()
                   .parseClaimsJws(token)
                   .getBody();
    }

    @Override
    public Key getKey(String secret) {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
}

