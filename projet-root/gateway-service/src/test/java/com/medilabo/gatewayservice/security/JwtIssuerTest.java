package com.medilabo.gatewayservice.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.medilabo.gatewayservice.jwt.JwtIssuer;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

class JwtIssuerTest {

    @Test
    void issue_builds_valid_token_with_roles() {
        String secret = "0123456789abcdefghijklmnopqrstuvwxyz012345";
        JwtIssuer issuer = new JwtIssuer(secret, 60L);

        String token = issuer.issue("alice", List.of(
                new SimpleGrantedAuthority("ROLE_PRATICIEN"),
                new SimpleGrantedAuthority("ROLE_ORGANISATEUR")
        ));

        Claims claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertEquals("alice", claims.getSubject());
        String roles = claims.get("roles", String.class);
        assertNotNull(roles);
        assertTrue(roles.contains("ROLE_PRATICIEN"));
        assertTrue(roles.contains("ROLE_ORGANISATEUR"));
        assertNotNull(claims.getExpiration());
        assertNotNull(claims.getIssuedAt());
    }
}
