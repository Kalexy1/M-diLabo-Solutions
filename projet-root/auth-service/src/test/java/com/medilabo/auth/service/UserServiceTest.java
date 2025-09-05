package com.medilabo.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.medilabo.auth.model.AppUser;
import com.medilabo.auth.repository.UserRepository;

class UserServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userService = new UserService(userRepository, passwordEncoder);
    }

    @Test
    void testUserExists_WhenUserExists_ReturnsTrue() {
        AppUser user = new AppUser();
        user.setUsername("existingUser");

        when(userRepository.findByUsername("existingUser")).thenReturn(user);

        assertTrue(userService.userExists("existingUser"));
    }

    @Test
    void testUserExists_WhenUserDoesNotExist_ReturnsFalse() {
        when(userRepository.findByUsername("newUser")).thenReturn(null);

        assertFalse(userService.userExists("newUser"));
    }

    @Test
    void testSaveUser_EncodesPasswordAndSavesUser() {
        AppUser user = new AppUser();
        user.setUsername("testuser");
        user.setPassword("plaintext");
        user.setRole("ROLE_PRATICIEN");

        when(passwordEncoder.encode("plaintext")).thenReturn("hashedPassword");
        when(userRepository.save(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AppUser savedUser = userService.saveUser(user);

        assertEquals("hashedPassword", savedUser.getPassword());
        verify(passwordEncoder).encode("plaintext");
        verify(userRepository).save(savedUser);
    }
}
