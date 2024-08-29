package com.gongyeon.io.netkim.model.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
    private final SecretKey secretKey;

    public JwtUtil(@Value("${spring.jwt.secret}")String key){
        this.secretKey = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
    }

    public long getMemberIdx(String token){
        return Long.parseLong(Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("memberIdx", String.class));
    }

    public String getRole(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("USER_ROLE", String.class);
    }

    public String createToken(long memberIdx, String userRole) {
        // 유효 시간 : 1시간 (1000 -> 1초 * 60 (1분) * 60 (1시간))
        Date exp = new Date(System.currentTimeMillis() + 1000*60*60*24*15);
        return Jwts.builder().
                header().add("typ", "JWT")
                .and().claim("memberIdx", Long.toString(memberIdx))
                .claim("USER_ROLE", userRole)
                .expiration(exp).signWith(secretKey)
                .compact();
    }

    public Jws<Claims> validate(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
    }
}