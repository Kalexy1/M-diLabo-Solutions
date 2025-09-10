package com.medilabo.patientservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuration de la sécurité pour le microservice Patient Service.
 * <p>
 * Cette configuration désactive la protection CSRF et permet l'accès à toutes les requêtes HTTP
 * sans authentification.
 * </p>
 */
@Configuration
public class SecurityConfig {

    /**
     * Définit la chaîne de filtres de sécurité pour l'application.
     * <p>
     * - Autorise toutes les requêtes sans authentification.<br>
     * - Désactive la protection CSRF (utile pour les API REST stateless).
     * </p>
     *
     * @param http l'objet {@link HttpSecurity} à configurer
     * @return la chaîne de filtres de sécurité {@link SecurityFilterChain}
     * @throws Exception en cas d'erreur de configuration
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .anyRequest().permitAll()
            )
            .csrf(csrf -> csrf.disable());
        return http.build();
    }
}
