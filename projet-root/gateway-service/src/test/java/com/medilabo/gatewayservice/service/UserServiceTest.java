package com.medilabo.gatewayservice.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.medilabo.gatewayservice.model.AppUser;
import com.medilabo.gatewayservice.repository.UserRepository;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindByUsername() {
        AppUser user = new AppUser();
        user.setUsername("john");

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));

        Optional<AppUser> result = userService.findByUsername("john");

        assertTrue(result.isPresent());
        assertEquals("john", result.get().getUsername());
        verify(userRepository).findByUsername("john");
    }

    @Test
    void testRegister() {
        AppUser user = new AppUser();
        user.setUsername("john");
        user.setPassword("pwd");

        when(passwordEncoder.encode("pwd")).thenReturn("ENC");
        when(userRepository.save(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AppUser saved = userService.register(user);

        assertEquals("john", saved.getUsername());
        assertEquals("ENC", saved.getPassword());
        verify(passwordEncoder).encode("pwd");
        verify(userRepository).save(any(AppUser.class));
    }

    @Test
    void testValidateCredentialsOk() {
        AppUser user = new AppUser();
        user.setUsername("john");
        user.setPassword("ENC");

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("pwd", "ENC")).thenReturn(true);

        assertTrue(userService.validateCredentials("john", "pwd"));
        verify(userRepository).findByUsername("john");
    }

    @Test
    void testValidateCredentialsFail() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertFalse(userService.validateCredentials("unknown", "pwd"));
        verify(userRepository).findByUsername("unknown");
    }
}
