package com.medilabo.auth.service;

import com.medilabo.auth.model.AppUser;
import com.medilabo.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class UserServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserService userService;

    @BeforeEach
    void setup() {
        userRepository = Mockito.mock(UserRepository.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        userService = new UserService(userRepository, passwordEncoder);
    }

    @Test
    void register_encodesPassword_and_normalizesUsername_and_checksUniqueness() {
        AppUser u = new AppUser();
        u.setUsername("  Alice ");
        u.setPassword("plain");

        when(userRepository.findByUsername("alice")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("plain")).thenReturn("{bcrypt}encoded");
        when(userRepository.save(any(AppUser.class))).thenAnswer(inv -> inv.getArgument(0));

        AppUser saved = userService.register(u);

        assertThat(saved.getUsername()).isEqualTo("alice");
        assertThat(saved.getPassword()).isEqualTo("{bcrypt}encoded");
    }

    @Test
    void register_throws_if_username_exists() {
        AppUser u = new AppUser();
        u.setUsername("bob");
        u.setPassword("pwd");

        when(userRepository.findByUsername("bob")).thenReturn(Optional.of(new AppUser()));

        assertThatThrownBy(() -> userService.register(u))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("déjà pris");
    }

    @Test
    void validateCredentials_returnsTrue_when_passwordMatches() {
        AppUser u = new AppUser();
        u.setUsername("bob");
        u.setPassword("{bcrypt}pw");

        when(userRepository.findByUsername("bob")).thenReturn(Optional.of(u));
        when(passwordEncoder.matches("pw-raw", "{bcrypt}pw")).thenReturn(true);

        assertThat(userService.validateCredentials("bob", "pw-raw")).isTrue();
    }

    @Test
    void validateCredentials_returnsFalse_when_userNotFound_or_passwordMismatch() {
        when(userRepository.findByUsername("john")).thenReturn(Optional.empty());
        assertThat(userService.validateCredentials("john", "x")).isFalse();

        AppUser u = new AppUser();
        u.setUsername("kate");
        u.setPassword("{bcrypt}pw");
        when(userRepository.findByUsername("kate")).thenReturn(Optional.of(u));
        when(passwordEncoder.matches("wrong", "{bcrypt}pw")).thenReturn(false);

        assertThat(userService.validateCredentials("kate", "wrong")).isFalse();
    }

    @Test
    void findByUsername_normalizesInput_toLowercase() {
        AppUser u = new AppUser();
        u.setUsername("med");

        when(userRepository.findByUsername("med")).thenReturn(Optional.of(u));

        Optional<AppUser> res = userService.findByUsername(" MeD ");
        assertThat(res).isPresent();
        assertThat(res.get().getUsername()).isEqualTo("med");
    }
}
