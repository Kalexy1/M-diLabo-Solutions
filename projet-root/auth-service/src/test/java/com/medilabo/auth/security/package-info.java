package com.medilabo.auth.security;

import com.nimbusds.jwt.SignedJWT;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.text.ParseException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtIssuerTest {

    @Test
    void shouldIssueValidJwtWithRoles() throws Exception {
        String secret = "this-is-a-very-long-test-secret-key-123456";
        JwtIssuer issuer = new JwtIssuer(secret);

        String token = issuer.issue(
                "orga",
                List.of(new SimpleGrantedAuthority("ROLE_ORGANISATEUR"))
        );

        assertNotNull(token);

        SignedJWT parsed = SignedJWT.parse(token);
        assertEquals("orga", parsed.getJWTClaimsSet().getSubject());
        assertTrue(parsed.getJWTClaimsSet().getStringListClaim("roles").contains("ROLE_ORGANISATEUR"));
        assertNotNull(parsed.getJWTClaimsSet().getIssueTime());
        assertNotNull(parsed.getJWTClaimsSet().getExpirationTime());
    }

    @Test
    void shouldThrowRuntimeExceptionWhenSecretIsInvalid() {
        String badSecret = "short";
        JwtIssuer issuer = new JwtIssuer(badSecret);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                issuer.issue("user", List.of())
        );
        assertTrue(ex.getMessage().contains("Cannot issue JWT"));
    }

    @Test
    void shouldIncludeMultipleRoles() throws ParseException {
        String secret = "another-very-long-test-secret-key-for-jwt-987654321";
        JwtIssuer issuer = new JwtIssuer(secret);

        String token = issuer.issue(
                "praticien",
                List.of(
                        new SimpleGrantedAuthority("ROLE_PRATICIEN"),
                        new SimpleGrantedAuthority("ROLE_USER")
                )
        );

        SignedJWT parsed = SignedJWT.parse(token);
        var roles = parsed.getJWTClaimsSet().getStringListClaim("roles");

        assertTrue(roles.contains("ROLE_PRATICIEN"));
        assertTrue(roles.contains("ROLE_USER"));
    }
}
