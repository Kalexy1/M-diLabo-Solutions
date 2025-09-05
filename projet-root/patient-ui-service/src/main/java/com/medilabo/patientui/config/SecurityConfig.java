package com.medilabo.patientui.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

/**
 * Application: com.medilabo.patientui.config
 * <p>
 * Classe <strong>SecurityConfig</strong>.
 * <br/>
 * Rôle : Configure la sécurité de l'application <em>patient-ui-service</em>.
 * </p>
 * <p>
 * Détails :
 * <ul>
 *   <li><b>CSRF activé</b> pour protéger les formulaires HTML (POST) utilisés dans l’UI.</li>
 *   <li>Accès public à <code>/access-denied</code>.</li>
 *   <li>Authentification requise pour toutes les autres requêtes.</li>
 *   <li>Page de connexion personnalisée : <code>/auth/login</code>.</li>
 *   <li>Redirection post-authentification vers <code>/patients</code>.</li>
 *   <li>Redirection en cas d'accès refusé : <code>/access-denied</code>.</li>
 * </ul>
 * </p>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    /**
     * Configure la chaîne de filtres Spring Security.
     *
     * @param http configuration {@link HttpSecurity}.
     * @return la {@link SecurityFilterChain} configurée.
     * @throws Exception en cas d'erreur de configuration.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/access-denied").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/auth/login").permitAll()
                .successHandler(successHandler())
            )
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/access-denied")
            )
            .build();
    }

    /**
     * Définit le handler exécuté après une authentification réussie.
     *
     * @return un {@link AuthenticationSuccessHandler} redirigeant vers <code>/patients</code>.
     */
    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return new SimpleUrlAuthenticationSuccessHandler("/patients");
    }
}
