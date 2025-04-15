package com.oauth2demo.basicsecurity.security;

import com.oauth2demo.basicsecurity.model.entity.UserEntity;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class JwtUtils {
    private final SecretKey secretKey = Jwts.SIG.HS256.key().build();
    private final Long expirationTime = 86400000L; // 1 day in milliseconds

    public String generateAccessToken(UserEntity user) {
        return generateToken(getExtraClaims(user), user);
    }

    private Map<String, Object> getExtraClaims(UserEntity user) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("authorities", user.getRoles());
        extraClaims.put("email", user.getEmail());
        return extraClaims;
    }

    public String generateToken(Map<String, Object> claims, UserEntity username) {
        return Jwts.builder()
                .subject(username.getUsername())
                .claims(claims)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(secretKey)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}
