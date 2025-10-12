package com.medilabo.auth.service;

import com.medilabo.auth.model.AppUser;
import com.medilabo.auth.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service personnalisé de chargement des utilisateurs pour Spring Security.
 * <p>
 * Cette classe implémente {@link UserDetailsService} et permet de récupérer
 * les informations d'un utilisateur à partir de la base de données
 * afin de les utiliser dans le processus d'authentification.
 * </p>
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    /**
     * Référentiel des utilisateurs permettant d'effectuer des recherches en base.
     */
    private final UserRepository userRepository;

    /**
     * Crée une instance du service {@code CustomUserDetailsService}.
     *
     * @param userRepository le repository utilisé pour accéder aux utilisateurs
     */
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Charge un utilisateur à partir de son nom d'utilisateur.
     * <p>
     * Le nom est normalisé (trim et mise en minuscules) avant la recherche.
     * Si l'utilisateur n'est pas trouvé, une {@link UsernameNotFoundException}
     * est levée.
     * </p>
     *
     * @param username le nom d'utilisateur à rechercher
     * @return un objet {@link UserDetails} contenant les informations nécessaires à Spring Security
     * @throws UsernameNotFoundException si aucun utilisateur ne correspond au nom fourni
     */
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
