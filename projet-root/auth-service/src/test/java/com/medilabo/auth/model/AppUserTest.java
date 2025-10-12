package com.medilabo.auth.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AppUserTest {

    @Test
    void springRole_is_prefixed_ROLE_and_matches_enum() {
        AppUser u = new AppUser();
        u.setRole(UserRole.ORGANISATEUR);
        assertThat(u.getSpringRole()).isEqualTo("ROLE_ORGANISATEUR");

        u.setRole(UserRole.PRATICIEN);
        assertThat(u.getSpringRole()).isEqualTo("ROLE_PRATICIEN");
    }
}
