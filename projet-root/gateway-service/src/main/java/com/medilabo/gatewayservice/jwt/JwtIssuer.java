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
 * Composant responsable de la génération des jetons JWT.
 *
 * <p>Ce service centralise la création des tokens signés et permet
 * l'émission de jetons avec un rôle unique ou une liste de rôles.</p>
 */
@Component
public class JwtIssuer {

    private final SecretKey key;
    private final long ttlSeconds;

    /**
     * Construit un émetteur de JWT en utilisant une clé secrète et une durée de vie
     * configurées par les propriétés Spring.
     *
     * @param secret     clé secrète utilisée pour signer les JWT
     * @param ttlSeconds durée de vie des jetons en secondes
     */
    public JwtIssuer(
            @Value("${security.jwt.secret:0123456789abcdefghijklmnopqrstuvwxyz012345}") String secret,
            @Value("${security.jwt.ttl-seconds:43200}") long ttlSeconds) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.ttlSeconds = ttlSeconds;
    }

    /**
     * Émet un JWT pour un utilisateur possédant un seul rôle.
     *
     * @param username nom d'utilisateur pour lequel générer le jeton
     * @param role     rôle attribué à l'utilisateur
     * @return un jeton JWT signé
     */
    public String issue(String username, String role) {
        return issue(username, List.of(() -> "ROLE_" + role));
    }

    /**
     * Émet un JWT pour un utilisateur avec plusieurs rôles.
     *
     * @param username     nom d'utilisateur
     * @param authorities  liste des autorités associées à l'utilisateur
     * @return un token JWT signé contenant les rôles et les métadonnées standard
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
