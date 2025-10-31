package com.medilabo.gatewayservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Configuration de la sécurité pour le microservice <strong>gateway-service</strong>.
 *
 * <p>
 * La gateway sécurise les accès aux microservices de Medilabo :
 * <ul>
 *   <li>Routes publiques : <code>/auth/**</code>, <code>/login</code>, <code>/register</code>,
 *       et les ressources statiques.</li>
 *   <li>Routes protégées : <code>/ui/**</code>, <code>/api/patients/**</code>,
 *       <code>/api/notes/**</code>, <code>/api/risk/**</code>.</li>
 *   <li>Désactivation des mécanismes inutiles (CSRF, HTTP Basic, form login).</li>
 * </ul>
 * </p>
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    /**
     * Chaîne de filtres WebFlux Security du gateway.
     *
     * <p>
     * - Sécurisé par défaut : toutes les routes sont protégées sauf celles explicitement autorisées.<br>
     * - Routes publiques : endpoints d’authentification, ressources statiques, santé (dev).<br>
     * - Routes protégées : espace UI et APIs métiers.
     * </p>
     *
     * @param http instance {@link ServerHttpSecurity} pour configurer la sécurité WebFlux
     * @return la {@link SecurityWebFilterChain} configurée
     */
    @Bean
    public SecurityWebFilterChain singleChain(ServerHttpSecurity http) {
        return http
            // La gateway ne gère pas de sessions ni de formulaires
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .cors(ServerHttpSecurity.CorsSpec::disable)
            .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
            .formLogin(ServerHttpSecurity.FormLoginSpec::disable)

            .authorizeExchange(ex -> ex
                // Préflight CORS
                .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // Routes publiques (authentification et ressources statiques)
                .pathMatchers("/auth/**", "/login", "/register").permitAll()
                .pathMatchers("/css/**", "/js/**", "/images/**", "/webjars/**", "/favicon.ico").permitAll()

                // Actuator minimal (public en dev)
                .pathMatchers("/actuator/health", "/actuator/info").permitAll()

                // Routes protégées : front UI et APIs internes
                .pathMatchers("/ui/**").authenticated()
                .pathMatchers("/api/patients/**", "/api/notes/**", "/api/risk/**").authenticated()

                // Bloque ou protège les probes navigateur
                .pathMatchers("/.well-known/**").authenticated()

                // Toute autre route non déclarée est protégée
                .anyExchange().authenticated()
            )
            .build();
    }
}
