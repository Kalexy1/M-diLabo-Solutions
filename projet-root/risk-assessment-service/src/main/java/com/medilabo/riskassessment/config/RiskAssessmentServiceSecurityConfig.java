package com.medilabo.riskassessment.config;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuration de sécurité du microservice d’évaluation du risque.
 * <p>
 * Les endpoints {@code /actuator/**} et les requêtes {@code OPTIONS} sont publics.
 * Les endpoints {@code /api/risk/**} nécessitent un JWT valide portant le rôle {@code PRATICIEN}.
 * </p>
 */
@Configuration
public class RiskAssessmentServiceSecurityConfig {

    /**
     * Définit la chaîne de filtres de sécurité pour le service d’évaluation du risque.
     *
     * @param http l’instance {@link HttpSecurity} utilisée pour configurer les règles de sécurité
     * @return la chaîne de filtres de sécurité configurée
     * @throws Exception en cas d’erreur de configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/api/risk/**").hasRole("PRATICIEN")
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth -> oauth
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            )
            .build();
    }

    /**
     * Crée un décodeur JWT HS256 basé sur un secret partagé avec le service d’authentification.
     *
     * @param secret clé secrète HMAC utilisée pour vérifier la signature des tokens
     * @return un {@link JwtDecoder} configuré pour HS256
     */
    @Bean
    public JwtDecoder jwtDecoder(@Value("${jwt.secret}") String secret) {
        SecretKey secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(secretKey).build();
    }

    /**
     * Convertit le claim {@code roles} du JWT en autorités Spring Security préfixées par {@code ROLE_}.
     *
     * @return un convertisseur d’authentification JWT mappant les rôles applicatifs
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        var converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            List<String> roles = jwt.getClaimAsStringList("roles");
            if (roles == null) {
                roles = List.of();
            }
            Collection<GrantedAuthority> authorities = roles.stream()
                    .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
            return authorities;
        });
        return converter;
    }
}
