package com.medilabo.gatewayservice.service;

import com.medilabo.gatewayservice.model.AppUser;
import com.medilabo.gatewayservice.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service handling user registration and lookup operations.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Returns {@code true} if a user with the given username already exists.
     *
     * @param username the username to check
     * @return {@code true} if the username is already used
     */
    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    /**
     * Registers a new user after encoding the password.
     *
     * @param user the user to register
     * @return the persisted user
     */
    public AppUser register(AppUser user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
}