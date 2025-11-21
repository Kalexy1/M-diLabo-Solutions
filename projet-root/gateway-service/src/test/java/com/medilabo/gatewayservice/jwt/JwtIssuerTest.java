package com.medilabo.gatewayservice.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtIssuerTest {

    private JwtIssuer issuer;
    private final String secret = "0123456789abcdefghijklmnopqrstuvwxyz012345"; // >= 32 chars
    private final long ttl = 3600;

    private SecretKey key;

    @BeforeEach
    void setUp() {
        issuer = new JwtIssuer(secret, ttl);
        key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // ----------------------------------------------------------------------
    // 1. TOKEN AVEC UN SEUL RÔLE
    // ----------------------------------------------------------------------
    @Test
    void testIssueSingleRole() {

        String token = issuer.issue("john", "ORGANISATEUR");

        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertEquals("john", claims.getSubject());
        assertEquals("ROLE_ORGANISATEUR", claims.get("roles"));
        assertNotNull(claims.getExpiration());
        assertNotNull(claims.getIssuedAt());
    }

    // ----------------------------------------------------------------------
    // 2. TOKEN AVEC PLUSIEURS RÔLES
    // ----------------------------------------------------------------------
    @Test
    void testIssueMultipleRoles() {

        List<SimpleGrantedAuthority> roles = List.of(
                new SimpleGrantedAuthority("ROLE_ORGANISATEUR"),
                new SimpleGrantedAuthority("ROLE_PRATICIEN")
        );

        String token = issuer.issue("alice", roles);

        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertEquals("alice", claims.getSubject());
        assertEquals("ROLE_ORGANISATEUR,ROLE_PRATICIEN", claims.get("roles"));
    }

    // ----------------------------------------------------------------------
    // 3. VALIDATION EXPIRATION (général)
    // ----------------------------------------------------------------------
    @Test
    void testExpirationIsInFuture() {

        String token = issuer.issue("bob", "ORGANISATEUR");

        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertTrue(claims.getExpiration().getTime() > claims.getIssuedAt().getTime());
    }
}
