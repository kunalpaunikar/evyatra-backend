package com.evyatra.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    // Generates the secret key used to sign JWT tokens
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // generate the Token
    public String generateToken(String email, String role) {
        return Jwts.builder()
                .subject(email)                    // 0.12.x mein setSubject() → subject()
                .claim("role", role)
                .issuedAt(new Date())              // setIssuedAt() → issuedAt()
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())         // algorithm auto detect hoga
                .compact();
    }

    // Extracts the email from the token
    public String getEmailFromToken(String token) {
        return Jwts.parser()                       // parserBuilder() → parser()
                .verifyWith(getSigningKey())        // setSigningKey() → verifyWith()
                .build()
                .parseSignedClaims(token)          // parseClaimsJws() → parseSignedClaims()
                .getPayload()
                .getSubject();
    }

    // is Token valid or Not ?
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public String getRoleFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);
    }
}