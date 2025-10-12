package com.medilabo.noteservice.config;

import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuration de la sécurité du microservice Note.
 * <p>
 * Configure la validation des JWT via Spring Security, définit les règles
 * d'autorisation et restreint l'accès aux endpoints aux utilisateurs ayant
 * le rôle {@code PRATICIEN} (sauf endpoints d'actuator).
 * </p>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class NoteServiceSecurityConfig {

    /**
     * Définit la chaîne de filtres de sécurité.
     *
     * @param http             l’objet {@link HttpSecurity} pour la configuration des règles de sécurité
     * @param jwtAuthConverter le convertisseur d’authentification JWT pour extraire les rôles
     * @return la chaîne de filtres de sécurité configurée
     * @throws Exception en cas d’erreur de configuration
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           JwtAuthenticationConverter jwtAuthConverter) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/**").permitAll()
                .anyRequest().hasRole("PRATICIEN")
            )
            .oauth2ResourceServer(oauth -> oauth
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter))
            );
        return http.build();
    }

    /**
     * Crée un décodeur JWT basé sur une clé secrète HMAC.
     *
     * @param secret la clé secrète utilisée pour valider les signatures JWT
     * @return un décodeur JWT configuré avec la clé HMAC
     */
    @Bean
    public JwtDecoder jwtDecoder(@Value("${jwt.secret}") String secret) {
        SecretKey key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(key).build();
    }

    /**
     * Configure le convertisseur d’authentification JWT pour extraire les rôles
     * depuis le claim {@code roles} en ajoutant le préfixe {@code ROLE_}.
     *
     * @return un convertisseur JWT configuré pour la gestion des rôles
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        var granted = new JwtGrantedAuthoritiesConverter();
        granted.setAuthoritiesClaimName("roles");
        granted.setAuthorityPrefix("ROLE_");

        var conv = new JwtAuthenticationConverter();
        conv.setJwtGrantedAuthoritiesConverter(granted);
        return conv;
    }
}
