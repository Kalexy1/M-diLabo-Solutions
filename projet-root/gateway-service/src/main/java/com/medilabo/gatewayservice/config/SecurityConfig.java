package com.medilabo.gatewayservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import java.net.URI;

/**
 * Configuration de la sécurité pour le microservice {@code gateway-service}.
 *
 * <p>
 * Ce gateway agit comme proxy sécurisé devant les autres microservices.  
 * Il valide les JWT signés émis par l’{@code auth-service} et applique les
 * règles d’accès en fonction des rôles contenus dans les jetons.
 * </p>
 *
 * <h3>Fonctionnalités principales</h3>
 * <ul>
 *   <li>Validation des jetons JWT (algorithme HS256) avec la clé secrète partagée
 *       définie par la variable d’environnement {@code JWT_SECRET}.</li>
 *   <li>Application de règles de sécurité par rôle :
 *     <ul>
 *       <li>{@code /auth/**}, {@code /access-denied} : accès public.</li>
 *       <li>{@code /ui/**}, {@code /patients/**} : accès réservé aux rôles {@code ORGANISATEUR} ou {@code PRATICIEN}.</li>
 *       <li>{@code /notes/**}, {@code /risk/**} : accès réservé au rôle {@code PRATICIEN}.</li>
 *       <li>Toutes les autres routes : authentification requise.</li>
 *     </ul>
 *   </li>
 *   <li>Redirection vers {@code /auth/login} si l’utilisateur n’est pas authentifié.</li>
 *   <li>Redirection vers {@code /access-denied} si l’utilisateur est authentifié
 *       mais ne possède pas les rôles requis.</li>
 *   <li>Désactivation du CSRF (le gateway ne sert pas de formulaires).</li>
 * </ul>
 */
@Configuration
public class SecurityConfig {

    /**
     * Définit la chaîne de filtres de sécurité pour Spring Security WebFlux.
     *
     * @param http l’API de configuration de la sécurité réactive
     * @return la configuration complète du filtre de sécurité
     */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
            .authorizeExchange(ex -> ex
                .pathMatchers("/auth/**", "/access-denied").permitAll()
                .pathMatchers("/ui/**", "/patients/**").hasAnyRole("ORGANISATEUR", "PRATICIEN")
                .pathMatchers("/notes/**", "/risk/**").hasRole("PRATICIEN")
                .anyExchange().authenticated()
            )
            .oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(ctx -> {
                var roles = (java.util.List<String>) ctx.getClaims().getOrDefault("roles", java.util.List.of());
                var auths = roles.stream()
                        .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
                        .map(org.springframework.security.core.authority.SimpleGrantedAuthority::new)
                        .toList();
                return reactor.core.publisher.Mono.just(
                        new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                                ctx.getSubject(), "n/a", auths));
            })))
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

    /**
     * Déclare un décodeur de jetons JWT pour l’algorithme HS256.
     *
     * <p>
     * Le décodeur est basé sur la clé secrète définie par la propriété
     * {@code JWT_SECRET}. Cette clé doit être identique à celle utilisée
     * par l’{@code auth-service} pour émettre les jetons.
     * </p>
     *
     * @param secret la clé secrète injectée depuis {@code JWT_SECRET}
     * @return un décodeur JWT réactif basé sur HS256
     */
    @Bean
    public ReactiveJwtDecoder jwtDecoder(@Value("${JWT_SECRET}") String secret) {
        return NimbusReactiveJwtDecoder.withSecretKey(
                new javax.crypto.spec.SecretKeySpec(secret.getBytes(), "HmacSHA256")).build();
    }
}
