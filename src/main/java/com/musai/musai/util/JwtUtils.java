package com.musai.musai.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

@Component
public class JwtUtils {

    private final String secretKey = "JWT_SECRET_KEY";

    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get("userId", Long.class); // JWT payload에서 userId 가져오기
    }
}
