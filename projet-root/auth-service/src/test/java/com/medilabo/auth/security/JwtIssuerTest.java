package com.medilabo.auth.security;

import com.nimbusds.jwt.SignedJWT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtIssuerTest {

    private JwtIssuer jwtIssuer;

    @BeforeEach
    void setUp() {
        // secret de 32+ octets
        String secret = "0123456789ABCDEF0123456789ABCDEF";
        jwtIssuer = new JwtIssuer(
                secret,
                3600,                     // TTL = 1h
                "test-issuer",            // issuer
                "gateway,patient",        // audience
                true,                     // include JTI
                true                      // include NBF
        );
    }

    @Test
    void issue_shouldGenerateValidToken_fromAuthorities() throws Exception {
        String token = jwtIssuer.issue("alice",
                List.of(new SimpleGrantedAuthority("ROLE_PRATICIEN")));
        SignedJWT parsed = SignedJWT.parse(token);

        assertThat(parsed.getJWTClaimsSet().getSubject()).isEqualTo("alice");
        assertThat(parsed.getJWTClaimsSet().getIssuer()).isEqualTo("test-issuer");
        assertThat(parsed.getJWTClaimsSet().getStringListClaim("roles")).containsExactly("PRATICIEN");
        assertThat(parsed.getJWTClaimsSet().getAudience()).contains("gateway", "patient");
        assertThat(parsed.getJWTClaimsSet().getExpirationTime()).isNotNull();
        assertThat(parsed.getJWTClaimsSet().getJWTID()).isNotNull();
    }

    @Test
    void issue_withAuthorities_shouldStripRolePrefix() throws Exception {
        String token = jwtIssuer.issue("bob",
                List.of(new SimpleGrantedAuthority("ROLE_ORGANISATEUR")));
        SignedJWT parsed = SignedJWT.parse(token);

        assertThat(parsed.getJWTClaimsSet().getStringListClaim("roles"))
                .containsExactly("ORGANISATEUR");
    }

    @Test
    void constructor_shouldRejectShortSecret() {
        String shortSecret = "too-short";
        assertThrows(IllegalArgumentException.class,
                () -> new JwtIssuer(shortSecret, 3600, "issuer", "gateway", true, true));
    }

    @Test
    void issue_withEmptyAuthorities_shouldHaveEmptyRolesClaim() throws Exception {
        String token = jwtIssuer.issue("charlie", List.of());
        SignedJWT parsed = SignedJWT.parse(token);

        assertThat(parsed.getJWTClaimsSet().getStringListClaim("roles")).isEmpty();
    }
}
