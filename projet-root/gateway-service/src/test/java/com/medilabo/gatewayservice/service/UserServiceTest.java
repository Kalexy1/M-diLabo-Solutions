package com.medilabo.gatewayservice.service;

import com.medilabo.gatewayservice.model.AppUser;
import com.medilabo.gatewayservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, passwordEncoder);
    }

    @Test
    void shouldEncodePasswordAndSaveUserOnRegister() {
        AppUser user = new AppUser();
        user.setUsername("john");
        user.setPassword("secret");
        when(passwordEncoder.encode("secret")).thenReturn("ENC(secret)");
        when(userRepository.save(user)).thenReturn(user);

        userService.register(user);

        verify(passwordEncoder).encode("secret");
        verify(userRepository).save(user);
        assertThat(user.getPassword()).isEqualTo("ENC(secret)");
    }

    @Test
    void shouldReturnTrueWhenUsernameExists() {
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(new AppUser()));
        assertThat(userService.existsByUsername("john")).isTrue();
    }

    @Test
    void shouldReturnFalseWhenUsernameDoesNotExist() {
        when(userRepository.findByUsername("john")).thenReturn(Optional.empty());
        assertThat(userService.existsByUsername("john")).isFalse();
    }
}