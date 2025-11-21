package com.medilabo.gatewayservice.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AppUserTest {

    @Test
    void testGettersAndSetters() {
        AppUser user = new AppUser();

        user.setId(1L);
        user.setUsername("john");
        user.setPassword("secret");
        user.setRole(UserRole.ORGANISATEUR);

        assertEquals(1L, user.getId());
        assertEquals("john", user.getUsername());
        assertEquals("secret", user.getPassword());
        assertEquals(UserRole.ORGANISATEUR, user.getRole());
    }

    @Test
    void testSpringRole() {
        AppUser user = new AppUser();
        user.setRole(UserRole.PRATICIEN);

        assertEquals("ROLE_PRATICIEN", user.getSpringRole());
    }

    @Test
    void testSpringRoleNull() {
        AppUser user = new AppUser();
        user.setRole(null);

        assertNull(user.getSpringRole());
    }

    @Test
    void testToString() {
        AppUser user = new AppUser(1L, "john", "pwd", UserRole.PRATICIEN);
        String result = user.toString();

        assertTrue(result.contains("john"));
        assertTrue(result.contains("PRATICIEN"));
    }
}
