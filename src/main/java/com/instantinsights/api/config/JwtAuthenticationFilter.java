package com.instantinsights.api.config;

import com.instantinsights.api.jwt.services.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.Key;

@Component
@Order(1)
public class JwtAuthenticationFilter implements Filter {

    private final JwtService jwtService;
    private final JwtConfig jwtConfig;

    public JwtAuthenticationFilter(JwtService jwtService, JwtConfig jwtConfig) {
        this.jwtService = jwtService;
        this.jwtConfig = jwtConfig;
    }

    @Override
    public void doFilter(
        ServletRequest request,
        ServletResponse response,
        FilterChain filterChain
    ) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String path = httpRequest.getRequestURI();

        if (path.startsWith("/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authorizationHeader = httpRequest.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String jwt = authorizationHeader.substring(7);
        Key key = jwtService.getKey(jwtConfig.getSecret());
        boolean isValid = jwtService.validateToken(jwt, key);

        if (!isValid) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Claims claims = jwtService.getClaims(jwt, key);
        String subject = claims.getSubject();
        request.setAttribute("subject", subject);
        filterChain.doFilter(request, response);
    }
}
