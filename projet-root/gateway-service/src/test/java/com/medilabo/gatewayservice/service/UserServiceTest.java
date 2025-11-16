package com.medilabo.gatewayservice.service;

import com.medilabo.gatewayservice.model.AppUser;
import com.medilabo.gatewayservice.model.UserRole;
import com.medilabo.gatewayservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Test
    void register_encodes_password_and_saves() {
        UserRepository repo = mock(UserRepository.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        when(encoder.encode("clear")).thenReturn("{bcrypt}hash");
        when(repo.save(any(AppUser.class))).thenAnswer(inv -> inv.getArgument(0));

        UserService service = new UserService(repo, encoder);

        AppUser u = new AppUser();
        u.setUsername("bob");
        u.setPassword("clear");
        u.setRole(UserRole.ORGANISATEUR);

        AppUser saved = service.register(u);

        assertEquals("bob", saved.getUsername());
        assertEquals("{bcrypt}hash", saved.getPassword());
        verify(repo).save(any(AppUser.class));
    }

    @Test
    void validateCredentials_uses_password_matches() {
        UserRepository repo = mock(UserRepository.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);

        AppUser u = new AppUser();
        u.setUsername("alice");
        u.setPassword("{bcrypt}hash");
        u.setRole(UserRole.PRATICIEN);

        when(repo.findByUsername("alice")).thenReturn(Optional.of(u));
        when(encoder.matches("pwd", "{bcrypt}hash")).thenReturn(true);

        UserService service = new UserService(repo, encoder);

        assertTrue(service.validateCredentials("alice","pwd"));
        verify(encoder).matches("pwd", "{bcrypt}hash");
    }
}
