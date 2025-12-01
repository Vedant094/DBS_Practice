package com.dbstraining.practice1.Security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // NOTE: Replace with secure key from env/config in production
    private final Key key = Keys.hmacShaKeyFor("THIS_IS_A_256_BIT_SECRET_KEY_CHANGE_IT_1234567890".getBytes());

    // generate token with optional role claim
    public String generateToken(String username) {
        return generateToken(username, null);
    }

    public String generateToken(String username, String role) {
        JwtBuilder builder = Jwts.builder()
                .setSubject(username)
                .claim("role",role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000)) // 24 hours
                .signWith(key, SignatureAlgorithm.HS256);

        if (role != null) {
            builder.claim("role", role);
        }

        return builder.compact();
    }

    // Extract username (subject)
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    // Extract role if present
    public String extractRole(String token) {
        Object r = extractAllClaims(token).get("role");
        return r != null ? r.toString() : null;
    }

    // Validate token by signature, expiration and matching username
    public boolean validateToken(String token, String usernameFromDB) {
        try {
            String username = extractUsername(token);
            return username.equals(usernameFromDB) && !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // Check expiration
    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    // Parse token
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}