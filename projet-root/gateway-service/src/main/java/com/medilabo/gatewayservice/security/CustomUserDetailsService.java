package com.medilabo.gatewayservice.security;

import com.medilabo.gatewayservice.model.AppUser;
import com.medilabo.gatewayservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import java.util.Collections;

/**
 * Service personnalisé permettant de charger les détails d'un utilisateur à partir de la base de données
 * pour l'authentification Spring Security.
 *
 * <p>Cette classe implémente {@link UserDetailsService} et surcharge la méthode
 * {@link #loadUserByUsername(String)} pour charger un utilisateur par son nom d'utilisateur
 * depuis la base de données via {@link UserRepository}.</p>
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Charge les détails d’un utilisateur par son nom d’utilisateur.
     *
     * <p>Cette méthode recherche l'utilisateur dans la base de données,
     * et convertit les données en un objet {@link UserDetails} utilisé
     * par Spring Security pour l'authentification.</p>
     *
     * @param username le nom d'utilisateur fourni lors de la connexion
     * @return les informations de l'utilisateur sous forme d'un {@link UserDetails}
     * @throws UsernameNotFoundException si l'utilisateur n'existe pas dans la base
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));

        return org.springframework.security.core.userdetails.User
            .withUsername(user.getUsername())
            .password(user.getPassword())
            .authorities(Collections.emptyList())
            .build();
    }
}