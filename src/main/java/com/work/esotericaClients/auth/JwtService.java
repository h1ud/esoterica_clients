package com.work.esotericaClients.auth;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
    private final String SECRET="347b777680ce26423debfd7d1c6c80b4d455caa3a85882a55c1ee3a7a6aadf06";
    private static final long JWT_EXPIRATION = 86400000;

    public SecretKey getSigningKey(){
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    public String generateToken(Long clientId){
        return Jwts.builder()
            .subject(clientId.toString())
            .issuedAt(new Date())
            .expiration(
                new Date(
                    System.currentTimeMillis()+JWT_EXPIRATION
                )
            )
            .signWith(getSigningKey())
            .compact();
    }

    public Long extractClientId(String token) {
        String subject = Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();

        return Long.valueOf(subject);
    }
}
