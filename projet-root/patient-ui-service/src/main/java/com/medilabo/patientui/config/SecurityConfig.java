package com.medilabo.patientui.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuration de sécurité minimale pour le service patient-ui.
 * <p>
 * Toute la gestion de l'authentification est déléguée à la gateway, ce service
 * autorise donc toutes les requêtes sans appliquer de règles spécifiques.
 * </p>
 */
@Configuration
public class SecurityConfig {

    /**
     * Autorise toutes les requêtes et désactive la protection CSRF, car les
     * contrôles d'accès sont appliqués au niveau de la gateway.
     *
     * @param http l'objet {@link HttpSecurity} à configurer
     * @return la chaîne de filtres de sécurité
     * @throws Exception en cas d'erreur de configuration
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .csrf(csrf -> csrf.disable());
        return http.build();
    }
}