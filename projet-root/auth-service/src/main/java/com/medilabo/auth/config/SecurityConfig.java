package com.medilabo.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.http.HttpMethod;

/**
 * Configuration de la sécurité du microservice d'authentification.
 * <p>
 * Cette classe définit la configuration des filtres de sécurité,
 * les règles d'accès aux endpoints, et le chiffrement des mots de passe.
 * </p>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configure la chaîne de filtres de sécurité Spring Security.
     * <p>
     * Cette configuration désactive CSRF, autorise certains endpoints publics,
     * redirige les requêtes non authentifiées vers la page de connexion
     * et utilise un mécanisme d’authentification personnalisé.
     * </p>
     *
     * @param http l'objet {@link HttpSecurity} utilisé pour configurer la sécurité HTTP
     * @return un {@link SecurityFilterChain} configuré
     * @throws Exception si une erreur de configuration survient
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, "/auth/login", "/auth/register", "/auth/logout").permitAll()
                .requestMatchers("/auth/**", "/login", "/register").permitAll()
                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**", "/actuator/**").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form.disable())
            .logout(logout -> logout.disable())
            .exceptionHandling(ex -> ex.authenticationEntryPoint((req, res, e) -> res.sendRedirect("/auth/login")));

        return http.build();
    }

    /**
     * Définit le bean responsable du chiffrement des mots de passe.
     * <p>
     * Utilise l'algorithme BCrypt pour un stockage sécurisé des mots de passe.
     * </p>
     *
     * @return une instance de {@link BCryptPasswordEncoder}
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
