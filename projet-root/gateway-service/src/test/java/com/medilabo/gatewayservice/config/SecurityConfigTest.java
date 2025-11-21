package com.medilabo.gatewayservice.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.JwtDecoder;

class SecurityConfigTest {

    private final SecurityConfig securityConfig = new SecurityConfig();

    @Test
    void jwtDecoder_shouldBeCreatedWithHmacKey() {
        String secret = "my-test-secret-key-123456789";

        JwtDecoder decoder = securityConfig.jwtDecoder(secret);

        assertThat(decoder).isNotNull();
    }

}
