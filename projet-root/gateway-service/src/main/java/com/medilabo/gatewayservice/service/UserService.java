package com.medilabo.gatewayservice.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Simple in-memory user service allowing dynamic registration of users.
 */
@Service
public class UserService implements UserDetailsService {

    private final Map<String, UserDetails> users = new ConcurrentHashMap<>();
    private final PasswordEncoder passwordEncoder;

    public UserService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        users.put("admin", User.withUsername("admin")
                                .password(passwordEncoder.encode("password"))
                                .roles("ADMIN")
                                .build());
    }

    /**
     * Register a new user with the given credentials.
     *
     * @param username the username
     * @param rawPassword the raw password
     * @param role user role
     */
    public void register(String username, String rawPassword, String role) {
        users.put(username, User.withUsername(username)
                                .password(passwordEncoder.encode(rawPassword))
                                .roles(role)
                                .build());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails user = users.get(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        return user;
    }
}