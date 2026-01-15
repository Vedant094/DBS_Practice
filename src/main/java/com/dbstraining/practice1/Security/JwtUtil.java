package com.dbstraining.practice1.Security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access.expiration}")
    private long accessExpiry;

    @Value("${jwt.refresh.expiration}")
    private long refreshExpiry;

    private Key key;

    @Value("${jwt.secret}")
    public void setKey(String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    /* ============================
           ACCESS TOKEN
       ============================ */
    public String generateAccessToken(String email, String role) {
        long now = System.currentTimeMillis();

        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .claim("typ", "access")
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + accessExpiry))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /* ============================
           REFRESH TOKEN (with role)
       ============================ */
    public String generateRefreshToken(String email, String role) {
        long now = System.currentTimeMillis();

        return Jwts.builder()
                .setSubject(email)
                .claim("typ", "refresh")
                .claim("role", role)         // ★ CRITICAL FIX ★
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + refreshExpiry))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isTokenValid(String token) {
        try { parseClaims(token); return true; }
        catch (Exception e) { return false; }
    }

    public boolean isRefreshToken(String token) {
        try { return "refresh".equals(parseClaims(token).get("typ")); }
        catch (Exception e) { return false; }
    }

    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public String extractRole(String token) {
        Object r = parseClaims(token).get("role");
        return r != null ? r.toString() : null;
    }
}

