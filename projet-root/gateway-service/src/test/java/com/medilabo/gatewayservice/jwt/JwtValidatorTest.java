package com.medilabo.gatewayservice.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;

import static org.junit.jupiter.api.Assertions.*;

class JwtValidatorTest {

    private JwtValidator validator;
    private final String secret = "0123456789abcdefghijklmnopqrstuvwxyz012345"; // >= 32 chars

    @BeforeEach
    void setup() throws Exception {
        validator = new JwtValidator();

        // Injection du champ private "secret"
        var field = JwtValidator.class.getDeclaredField("secret");
        field.setAccessible(true);
        field.set(validator, secret);
    }

    @Test
    void testValidateValidToken() {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());

        String token = Jwts.builder()
                .subject("alice")
                .claim("roles", "ROLE_ORGANISATEUR")
                .signWith(key)
                .compact();

        Claims claims = validator.validate(token);

        assertEquals("alice", claims.getSubject());
        assertEquals("ROLE_ORGANISATEUR", claims.get("roles"));
    }

    @Test
    void testValidateInvalidToken() {
        assertThrows(Exception.class, () -> validator.validate("invalid.token.value"));
    }
}
