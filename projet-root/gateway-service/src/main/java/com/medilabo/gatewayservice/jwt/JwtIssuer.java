package com.medilabo.gatewayservice.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service responsable de la génération des JWT.
 * Compatible avec injection Spring et tests unitaires.
 */
@Component
public class JwtIssuer {

    private final SecretKey key;
    private final long ttlSeconds;

    /**
     * Constructeur utilisé à la fois par Spring et par les tests.
     * (Spring injecte automatiquement les valeurs via @Value.)
     */
    public JwtIssuer(
            @Value("${security.jwt.secret:0123456789abcdefghijklmnopqrstuvwxyz012345}") String secret,
            @Value("${security.jwt.ttl-seconds:43200}") long ttlSeconds) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.ttlSeconds = ttlSeconds;
    }

    /**
     * Émet un token avec un seul rôle (utilisé dans le contrôleur).
     */
    public String issue(String username, String role) {
        return issue(username, List.of(() -> "ROLE_" + role));
    }

    /**
     * Émet un token avec une liste de rôles (utilisé dans les tests).
     */
    public String issue(String username, List<? extends GrantedAuthority> authorities) {
        Instant now = Instant.now();
        String roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .subject(username)
                .claim("roles", roles)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(ttlSeconds)))
                .signWith(key)
                .compact();
    }
}
