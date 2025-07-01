package com.medilabo.patientui.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuration de la sécurité pour le microservice patient-ui.
 * 
 * <p>Cette configuration définit les règles d'authentification et d'autorisation
 * pour les utilisateurs de l'application, ainsi que la gestion du formulaire de login.
 * La protection CSRF est activée par défaut pour protéger contre les attaques CSRF.</p>
 */
@Configuration
public class SecurityConfig {

    /**
     * Configure la chaîne de filtres de sécurité de Spring Security.
     *
     * <p>Définit les règles d'accès aux URLs :
     * <ul>
     *     <li>Les ressources statiques (css, js, images) et les pages d'inscription et connexion sont accessibles à tous.</li>
     *     <li>Les URL sécurisées (patients, notes, risk) nécessitent un rôle ORGANISATEUR ou PRATICIEN.</li>
     *     <li>Toutes les autres requêtes nécessitent une authentification.</li>
     * </ul>
     * Le formulaire de connexion personnalisé est configuré, ainsi que la gestion des sessions et déconnexions.
     * <br/>
     * La protection CSRF est activée par défaut et ne doit pas être désactivée pour assurer la sécurité des formulaires.</p>
     *
     * @param http l'objet HttpSecurity à configurer
     * @return la chaîne de filtres de sécurité configurée
     * @throws Exception en cas d'erreur lors de la configuration
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/css/**", "/js/**", "/images/**", "/register", "/login").permitAll()
                .requestMatchers("/patients/**", "/notes/**","/risk/**").hasAnyRole("ORGANISATEUR", "PRATICIEN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/patients", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            .httpBasic();

        return http.build();
    }

    /**
     * Fournit un encodeur de mot de passe basé sur l’algorithme BCrypt.
     *
     * <p>Ce bean est utilisé pour encoder et vérifier les mots de passe utilisateurs
     * de manière sécurisée avant stockage et lors de l'authentification.</p>
     *
     * @return une instance de {@link BCryptPasswordEncoder}
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
