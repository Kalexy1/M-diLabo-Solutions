package com.medilabo.patientui.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests unitaires de SecurityConfig sans charger le contexte Spring.
 *
 * On instancie directement SecurityConfig et on teste le JwtAuthenticationConverter.
 */
class SecurityConfigTest {

    private final SecurityConfig securityConfig = new SecurityConfig();
    private final JwtAuthenticationConverter converter = securityConfig.jwtAuthenticationConverter();

    @Test
    void shouldExtractRolesFromList() {
        Jwt jwt = Jwt.withTokenValue("test-token")
                .header("alg", "none")
                .claim("roles", List.of("ADMIN", "PRATICIEN"))
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(60))
                .build();

        var auth = converter.convert(jwt);

        assertThat(auth).isNotNull();
        List<String> authorities = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        assertThat(authorities)
                .containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_PRATICIEN");
    }

    @Test
    void shouldExtractRolesFromString() {
        Jwt jwt = Jwt.withTokenValue("test-token")
                .header("alg", "none")
                .claim("roles", "ROLE_PRATICIEN,ROLE_ORGANISATEUR")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(60))
                .build();

        var auth = converter.convert(jwt);

        assertThat(auth).isNotNull();
        List<String> authorities = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        assertThat(authorities)
                .containsExactlyInAnyOrder("ROLE_PRATICIEN", "ROLE_ORGANISATEUR");
    }

    @Test
    void shouldExtractAuthoritiesFromList() {
        Jwt jwt = Jwt.withTokenValue("test-token")
                .header("alg", "none")
                .claim("authorities", List.of("MEDIC", "HELPER"))
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(60))
                .build();

        var auth = converter.convert(jwt);

        assertThat(auth).isNotNull();
        List<String> authorities = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        assertThat(authorities)
                .containsExactlyInAnyOrder("ROLE_MEDIC", "ROLE_HELPER");
    }

    @Test
    void shouldMergeRolesAndAuthorities() {
        Jwt jwt = Jwt.withTokenValue("test-token")
                .header("alg", "none")
                .claim("roles", List.of("ADMIN"))
                .claim("authorities", List.of("MEDIC"))
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(60))
                .build();

        var auth = converter.convert(jwt);

        assertThat(auth).isNotNull();
        List<String> authorities = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        assertThat(authorities)
                .containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_MEDIC");
    }
}
