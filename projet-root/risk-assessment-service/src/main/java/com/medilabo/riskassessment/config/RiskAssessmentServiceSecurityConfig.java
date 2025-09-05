package com.medilabo.riskassessment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Application: com.medilabo.riskassessment.config
 * <p>
 * Classe <strong>RiskAssessmentServiceSecurityConfig</strong>.
 * <br/>
 * Rôle : Configure la sécurité pour le microservice <em>risk-assessment-service</em>.
 * </p>
 * <p>
 * Détails de la configuration :
 * <ul>
 *   <li><b>CSRF désactivé</b> car ce microservice expose uniquement des endpoints REST
 *   et ne gère pas de formulaires HTML.</li>
 *   <li><b>Authentification obligatoire</b> pour toutes les requêtes.</li>
 *   <li><b>Authentification HTTP Basic</b> activée pour la simplicité
 *   des appels entre microservices.</li>
 *   <li>Support des annotations de sécurité méthode comme
 *   {@code @PreAuthorize} grâce à {@link EnableMethodSecurity}.</li>
 * </ul>
 * </p>
 */
@Configuration
@EnableMethodSecurity
public class RiskAssessmentServiceSecurityConfig {

    /**
     * Définit la chaîne de filtres de sécurité.
     *
     * @param http instance de {@link HttpSecurity} à configurer
     * @return une {@link SecurityFilterChain} configurée
     * @throws Exception en cas d’erreur de configuration
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
