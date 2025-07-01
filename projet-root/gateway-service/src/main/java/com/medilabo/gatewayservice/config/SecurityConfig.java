package com.medilabo.gatewayservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuration de la sécurité pour le service Gateway.
 * <p>
 * Cette classe désactive la protection CSRF (car la gateway n'a pas de session) et
 * autorise toutes les requêtes sans authentification. Elle peut être modifiée ultérieurement
 * pour intégrer une authentification globale (comme OAuth2 ou JWT).
 * </p>
 *
 * <p>
 * Cette configuration s'applique à toutes les routes exposées par Spring Cloud Gateway.
 * </p>
 *
 */
@Configuration
public class SecurityConfig {

    /**
     * Définit la chaîne de filtres de sécurité HTTP.
     * <p>
     * Cette méthode désactive la protection CSRF et autorise toutes les requêtes entrantes
     * sans restriction. Elle est utile pour des tests ou pour une gateway qui ne gère pas
     * directement l'authentification.
     * </p>
     *
     * @param http l'objet {@link HttpSecurity} configuré par Spring Security
     * @return un {@link SecurityFilterChain} configuré pour cette application
     * @throws Exception en cas d'erreur de configuration
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .build();
    }
}
