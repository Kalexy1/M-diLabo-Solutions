package com.medilabo.patientservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Application: com.medilabo.patientservice.config
 * <p>
 * Classe <strong>PatientServiceSecurityConfig</strong>.
 * <br/>
 * Rôle : Configuration de la sécurité pour le microservice <em>patient-service</em>.
 * Active la sécurité via formulaire et l'utilisation des annotations {@code @PreAuthorize}.
 * </p>
 * <p>
 * Détails :
 * <ul>
 *   <li>CSRF désactivé (API REST derrière la Gateway).</li>
 *   <li>La page <code>/access-denied</code> est accessible publiquement.</li>
 *   <li>Toutes les autres requêtes nécessitent une authentification.</li>
 *   <li>Form login délégué à la page <code>/auth/login</code> (exposée via Gateway).</li>
 *   <li>Déconnexion sur <code>/logout</code> avec redirection vers <code>/auth/login?logout</code>.</li>
 *   <li>Gestion d'accès refusé : redirection vers la vue <code>/access-denied</code>.</li>
 * </ul>
 * </p>
 */
@Configuration
@EnableMethodSecurity
public class PatientServiceSecurityConfig {

    /**
     * Configure la chaîne de filtres Spring Security pour le service patient.
     *
     * @param http objet de configuration HTTP Security.
     * @return la {@link SecurityFilterChain} configurée.
     * @throws Exception en cas de problème de configuration.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/access-denied").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/auth/login")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/auth/login?logout")
                .permitAll()
            )
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/access-denied")
            )
            .build();
    }
}
