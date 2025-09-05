package com.medilabo.gatewayservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import java.net.URI;

/**
 * Application: com.medilabo.gatewayservice.config
 * <p>
 * Classe <strong>SecurityConfig</strong>.
 * <br/>
 * Rôle : Configure la sécurité (Spring Security WebFlux) du Gateway.
 * </p>
 *
 * <h3>Principes</h3>
 * <ul>
 *   <li>Le Gateway ne gère <em>pas</em> de formulaire de login : la route <code>/auth/**</code>
 *       (gérée par <em>auth-service</em>) est responsable de l’authentification.</li>
 *   <li>Les accès aux routes sont filtrés au bord (roles/permissions) avant de proxyfier les appels.</li>
 *   <li>CSRF est désactivé : le Gateway ne rend pas de pages ni de formulaires côté serveur.</li>
 *   <li>Non authentifié → redirection 303 vers <code>/auth/login</code>.</li>
 *   <li>Accès refusé (rôle insuffisant) → redirection 303 vers <code>/access-denied</code>.</li>
 * </ul>
 */
@Configuration
public class SecurityConfig {

    /**
     * Chaîne de filtres de sécurité pour Spring Cloud Gateway.
     *
     * <p><strong>Règles d’autorisation :</strong></p>
     * <ul>
     *   <li><code>/auth/**</code> et <code>/access-denied</code> : accès public.</li>
     *   <li><code>/ui/**</code> et <code>/patients/**</code> : rôles <code>ORGANISATEUR</code> ou <code>PRATICIEN</code>.</li>
     *   <li><code>/notes/**</code> et <code>/risk/**</code> : rôle <code>PRATICIEN</code> uniquement.</li>
     *   <li>toute autre route : authentification requise.</li>
     * </ul>
     *
     * <p><strong>Gestion des erreurs :</strong></p>
     * <ul>
     *   <li>Non authentifié : redirection 303 → <code>/auth/login</code>.</li>
     *   <li>Accès refusé : redirection 303 → <code>/access-denied</code>.</li>
     * </ul>
     *
     * @param http l’API de configuration de la sécurité WebFlux
     * @return la chaîne de filtres de sécurité
     */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
            .authorizeExchange(exchanges -> exchanges
                .pathMatchers("/auth/**", "/access-denied").permitAll()
                .pathMatchers("/ui/**", "/patients/**").hasAnyRole("ORGANISATEUR", "PRATICIEN")
                .pathMatchers("/notes/**", "/risk/**").hasRole("PRATICIEN")
                .anyExchange().authenticated()
            )
            .exceptionHandling(e -> e
                .authenticationEntryPoint((exchange, ex) -> {
                    exchange.getResponse().setStatusCode(HttpStatus.SEE_OTHER);
                    exchange.getResponse().getHeaders().setLocation(URI.create("/auth/login"));
                    return exchange.getResponse().setComplete();
                })
                .accessDeniedHandler((exchange, denied) -> {
                    exchange.getResponse().setStatusCode(HttpStatus.SEE_OTHER);
                    exchange.getResponse().getHeaders().setLocation(URI.create("/access-denied"));
                    return exchange.getResponse().setComplete();
                })
            )
            .csrf(ServerHttpSecurity.CsrfSpec::disable)

            .build();
    }
}
