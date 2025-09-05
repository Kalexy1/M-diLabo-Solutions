package com.medilabo.noteservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Application: com.medilabo.noteservice.config
 * <p>
 * Classe <strong>NoteServiceSecurityConfig</strong>.
 * <br/>
 * Rôle : Configuration de la sécurité pour le microservice note-service.
 * Active la sécurité HTTP basique et autorise les annotations @PreAuthorize.
 * </p>
 */
@Configuration
@EnableMethodSecurity
public class NoteServiceSecurityConfig {

    /**
     * Configure la chaîne de filtres de sécurité :
     * <ul>
     *   <li>Désactive CSRF (utile pour les APIs REST)</li>
     *   <li>Nécessite une authentification pour toutes les requêtes</li>
     *   <li>Utilise l'authentification HTTP Basic (gérée en amont par la Gateway)</li>
     * </ul>
     *
     * @param http configuration HTTP
     * @return le filtre de sécurité
     * @throws Exception en cas de mauvaise configuration
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf().disable()
            .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
            .httpBasic()
            .and()
            .build();
    }
}
