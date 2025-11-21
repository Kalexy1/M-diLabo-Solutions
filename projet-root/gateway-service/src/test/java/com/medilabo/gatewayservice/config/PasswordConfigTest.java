package com.medilabo.gatewayservice.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class PasswordConfigTest {

    @Test
    void testPasswordEncoderBean() {
        PasswordConfig config = new PasswordConfig();
        PasswordEncoder encoder = config.passwordEncoder();

        String hash = encoder.encode("password");

        assertNotNull(hash);
        assertTrue(encoder.matches("password", hash));
    }
}
