package com.medilabo.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.medilabo.auth.model.AppUser;
import com.medilabo.auth.repository.UserRepository;

class CustomUserDetailsServiceTest {

    @Test
    void shouldLoadUserByUsername() {
        UserRepository mockRepo = mock(UserRepository.class);
        AppUser user = new AppUser();
        user.setUsername("test");
        user.setPassword("pass");
        user.setRole("ROLE_PRATICIEN");

        when(mockRepo.findByUsername("test")).thenReturn(user);

        CustomUserDetailsService service = new CustomUserDetailsService(mockRepo);
        var result = service.loadUserByUsername("test");

        assertEquals("test", result.getUsername());
        assertEquals("pass", result.getPassword());
        assertTrue(result.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_PRATICIEN")));
    }

    @Test
    void shouldThrowIfUserNotFound() {
        UserRepository mockRepo = mock(UserRepository.class);
        when(mockRepo.findByUsername("notfound")).thenReturn(null);

        CustomUserDetailsService service = new CustomUserDetailsService(mockRepo);

        assertThrows(UsernameNotFoundException.class, () -> {
            service.loadUserByUsername("notfound");
        });
    }
}
