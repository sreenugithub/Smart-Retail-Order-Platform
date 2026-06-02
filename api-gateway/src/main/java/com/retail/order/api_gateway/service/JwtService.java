package com.retail.order.api_gateway.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    public void validateToken(String token) {

        Jwts.parser()
                .verifyWith(
                        Keys.hmacShaKeyFor(secret.getBytes())
                )
                .build()
                .parseSignedClaims(token);
    }

    public Claims extractClaims(String token) {

        return Jwts.parser()
                .verifyWith(
                        Keys.hmacShaKeyFor(secret.getBytes())
                )
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}