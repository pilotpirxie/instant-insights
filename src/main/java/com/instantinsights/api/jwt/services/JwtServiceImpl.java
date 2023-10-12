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
    public String generateToken(String subject, Key key, Long expirationMillis, String tokenId) {
        return Jwts.builder()
                   .setSubject(subject)
                   .setIssuedAt(new Date())
                   .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
                   .setId(tokenId)
                   .signWith(key, SignatureAlgorithm.HS256)
                   .compact();
    }

    @Override
    public boolean validateToken(String token, Key key) {

        String algorithm = Jwts.jwsHeader().getAlgorithm();
        if (!algorithm.equals("HS256")) {
            return false;
        }

        return Jwts.parserBuilder()
                   .setSigningKey(key)
                   .build()
                   .parseClaimsJws(token)
                   .getBody()
                   .getExpiration()
                   .after(new Date());
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                   .build()
                   .parseClaimsJws(token)
                   .getBody();
    }

    @Override
    public Key getKey(String secret) {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
}

