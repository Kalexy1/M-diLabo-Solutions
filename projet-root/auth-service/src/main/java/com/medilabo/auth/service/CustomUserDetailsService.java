package com.medilabo.auth.service;

import com.medilabo.auth.model.AppUser;
import com.medilabo.auth.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser u = userRepository.findByUsername(username == null ? null : username.trim().toLowerCase())
            .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable : " + username));

        return User.withUsername(u.getUsername())
                .password(u.getPassword())
                .authorities(new SimpleGrantedAuthority("ROLE_" + u.getRole().name()))
                .build();
    }
}
