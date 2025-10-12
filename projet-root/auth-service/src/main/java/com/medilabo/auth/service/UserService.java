package com.medilabo.auth.service;

import com.medilabo.auth.model.AppUser;
import com.medilabo.auth.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private String normalize(String username) {
        return username == null ? null : username.trim().toLowerCase();
    }

    /** Inscrit un user (unicité + encodage BCrypt). */
    public AppUser register(AppUser user) {
        String norm = normalize(user.getUsername());
        if (userRepository.findByUsername(norm).isPresent()) {
            throw new IllegalArgumentException("Nom d'utilisateur déjà pris.");
        }
        user.setUsername(norm);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    /** Valide identifiants (compare BCrypt). */
    public boolean validateCredentials(String username, String rawPassword) {
        return findByUsername(username)
                .map(u -> passwordEncoder.matches(rawPassword, u.getPassword()))
                .orElse(false);
    }

    /** Lookup par username (stocké en lowercase). */
    public Optional<AppUser> findByUsername(String username) {
        return userRepository.findByUsername(normalize(username));
    }
}
