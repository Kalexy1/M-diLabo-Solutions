package com.medilabo.gatewayservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Configuration de la sécurité pour le microservice <strong>gateway-service</strong>.
 * <p>
 * Cette classe définit la configuration de sécurité WebFlux appliquée à la passerelle,
 * désactive les mécanismes non nécessaires (CSRF, formulaires, authentification basique),
 * et autorise certaines routes publiques (authentification, ressources statiques, etc.).
 * </p>
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    /**
     * Configure la chaîne de filtres de sécurité WebFlux.
     * <p>
     * - Désactive CSRF, le login par formulaire et l’authentification HTTP Basic. <br>
     * - Autorise les requêtes vers les routes publiques telles que :
     *   <ul>
     *     <li><code>/auth/**</code>, <code>/login</code>, <code>/register</code></li>
     *     <li>les ressources statiques : <code>/css/**</code>, <code>/js/**</code>,
     *         <code>/images/**</code>, <code>/webjars/**</code></li>
     *     <li>les endpoints d’administration : <code>/actuator/**</code></li>
     *   </ul>
     * - Toutes les autres requêtes sont également autorisées (aucune validation au niveau du gateway).
     * </p>
     *
     * @param http l’objet {@link ServerHttpSecurity} permettant de configurer la sécurité
     * @return la chaîne de filtres de sécurité configurée
     */
    @Bean
    public SecurityWebFilterChain singleChain(ServerHttpSecurity http) {
        return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .cors(Customizer.withDefaults())
            .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
            .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
            .authorizeExchange(ex -> ex
                .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .pathMatchers("/auth/**", "/login", "/register").permitAll()
                .pathMatchers("/css/**", "/js/**", "/images/**", "/webjars/**", "/favicon.ico").permitAll()
                .pathMatchers("/actuator/**").permitAll()
                .anyExchange().permitAll()
            )
            .build();
    }
}
