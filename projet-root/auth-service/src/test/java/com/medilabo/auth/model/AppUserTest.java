package com.medilabo.auth.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class AppUserTest {

    @Test
    void shouldSetRoleWithPrefixIfMissing() {
        AppUser user = new AppUser();
        user.setRole("praticien");
        assertEquals("ROLE_PRATICIEN", user.getRole());
    }

    @Test
    void shouldThrowForInvalidRole() {
        AppUser user = new AppUser();
        assertThrows(IllegalArgumentException.class, () -> user.setRole("admin"));
    }
}
