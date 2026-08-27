package org.tracker.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.tracker.model.entities.User;
import org.tracker.service.JwtService;

import javax.crypto.SecretKey;
import java.util.Date;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
public class JwtServiceImpl implements JwtService {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.duration}")
    private Duration tokenDuration;

    public SecretKey getSigningKey(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    @Override
    public String createJwtToken(User user) {
        String id = user.getId().toString();
        String email = user.getEmail();
        Date now = Date.from(Instant.now());
        Date expiry = Date.from(Instant.now().plus(tokenDuration));

        return Jwts.builder()
                .subject(id)
                .claim("email", email)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(getSigningKey())
                .compact();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public UUID extractId(String token){
        return UUID.fromString(extractAllClaims(token).getSubject());
    }

    public String extractEmail(String token){
        return extractAllClaims(token).get("email", String.class);
    }
}
