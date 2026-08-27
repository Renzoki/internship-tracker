package org.tracker.service.impl;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.tracker.model.entities.User;
import org.tracker.service.JwtService;

import java.util.Date;
import java.time.Duration;
import java.time.Instant;

@Service
public class JwtServiceImpl implements JwtService {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.duration}")
    private Duration tokenDuration;

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
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)))
                .compact();
    }
}
