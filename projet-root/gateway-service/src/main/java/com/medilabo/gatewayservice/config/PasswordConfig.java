package com.medilabo.gatewayservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration du système d'encodage des mots de passe.
 *
 * <p>Cette classe fournit un bean {@link PasswordEncoder} utilisé par
 * l'application pour chiffrer les mots de passe à l'aide de l'algorithme
 * {@link BCryptPasswordEncoder}.</p>
 */
@Configuration
public class PasswordConfig {

    /**
     * Déclare un bean {@link PasswordEncoder} basé sur BCrypt.
     *
     * @return une instance de {@link BCryptPasswordEncoder}
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
