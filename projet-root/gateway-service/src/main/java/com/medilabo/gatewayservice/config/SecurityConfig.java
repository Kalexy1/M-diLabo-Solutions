package com.medilabo.gatewayservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

/**
 * Application: gateway-service
 *
 * <p><strong>SecurityConfig</strong></p>
 *
 * <p>
 * Configuration Spring Security (WebFlux) pour la Gateway.
 * La Gateway valide les JWT émis par <em>auth-service</em> (HS256) et
 * applique les règles d’accès en fonction des rôles présents dans le jeton.
 * </p>
 *
 * <h3>Fonctionnalités</h3>
 * <ul>
 *   <li>Validation des JWT via {@link ReactiveJwtDecoder} (secret {@code JWT_SECRET}).</li>
 *   <li>Accès public : {@code /auth/**}, {@code /access-denied}, {@code /actuator/**}
 *       et ressources statiques ({@code /favicon.ico}, {@code /webjars/**}, {@code /css/**},
 *       {@code /js/**}, {@code /images/**}).</li>
 *   <li>Accès {@code /ui/**} et {@code /patients/**} : rôles {@code ORGANISATEUR} ou {@code PRATICIEN}.</li>
 *   <li>Accès {@code /notes/**} et {@code /risk/**} : rôle {@code PRATICIEN}.</li>
 *   <li>Redirection vers {@code /auth/login} si non authentifié, vers {@code /access-denied} si interdit.</li>
 *   <li>CSRF désactivé (la Gateway ne sert pas de formulaires).</li>
 * </ul>
 */
@Configuration
public class SecurityConfig {

    /**
     * Chaîne des filtres de sécurité réactive.
     */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
            .authorizeExchange(ex -> ex
                .pathMatchers(
                    "/auth/**",
                    "/access-denied",
                    "/actuator/**",
                    "/favicon.ico",
                    "/webjars/**",
                    "/css/**",
                    "/js/**",
                    "/images/**"
                ).permitAll()

                .pathMatchers("/ui/**", "/patients/**").hasAnyRole("ORGANISATEUR", "PRATICIEN")
                .pathMatchers("/notes/**", "/risk/**").hasRole("PRATICIEN")

                .anyExchange().authenticated()
            )
            .oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt
                .jwtAuthenticationConverter(token -> {
                    Object rolesClaim = token.getClaims().get("roles");
                    Object authsClaim = token.getClaims().get("authorities");

                    Stream<String> rolesStream = Stream.empty();
                    if (rolesClaim instanceof Collection<?> c1) {
                        rolesStream = c1.stream().filter(String.class::isInstance).map(String.class::cast);
                    }
                    Stream<String> authsStream = Stream.empty();
                    if (authsClaim instanceof Collection<?> c2) {
                        authsStream = c2.stream().filter(String.class::isInstance).map(String.class::cast);
                    }

                    List<SimpleGrantedAuthority> authorities = Stream
                        .concat(rolesStream, authsStream)
                        .distinct()
                        .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
                        .map(SimpleGrantedAuthority::new)
                        .toList();

                    return reactor.core.publisher.Mono.just(
                        new UsernamePasswordAuthenticationToken(token.getSubject(), "n/a", authorities)
                    );
                })
            ))
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
     * Décodeur JWT HS256 basé sur la clé partagée avec {@code auth-service}.
     *
     * @param secret valeur de la propriété/variable d’environnement {@code JWT_SECRET}
     */
    @Bean
    public ReactiveJwtDecoder jwtDecoder(@Value("${JWT_SECRET}") String secret) {
        var key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return NimbusReactiveJwtDecoder.withSecretKey(key).build();
    }
}
