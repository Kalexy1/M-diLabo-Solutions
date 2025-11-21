package com.medilabo.gatewayservice.config;

import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Configuration de la sécurité du microservice Gateway.
 *
 * <p>Cette classe définit les règles d'accès HTTP, la politique CORS, la
 * gestion des sessions et l'encodage JWT utilisé pour décoder manuellement
 * les jetons si nécessaire.</p>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configure la chaîne de filtres Spring Security.
     *
     * <p>Cette configuration désactive CSRF, active CORS, utilise une
     * gestion de session stateless et définit les règles d'accès aux
     * différents endpoints du gateway. Le Resource Server JWT est
     * explicitement désactivé.</p>
     *
     * @param http l'objet {@link HttpSecurity} à configurer
     * @return la configuration de sécurité sous forme de {@link SecurityFilterChain}
     * @throws Exception en cas d'erreur de configuration
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> {})
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**", "/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                .requestMatchers("/ui/**").permitAll()
                .requestMatchers("/api/**").permitAll()
                .anyRequest().permitAll()
            )
            .oauth2ResourceServer(oauth2 -> oauth2.disable());

        return http.build();
    }

    /**
     * Crée un décodeur JWT basé sur une clé secrète HMAC.
     *
     * <p>Ce bean est destiné à un usage manuel et n'est pas utilisé par
     * Spring Security dans cette configuration.</p>
     *
     * @param secret la clé secrète définie dans la configuration de l'application
     * @return une instance de {@link JwtDecoder}
     */
    @Bean
    public JwtDecoder jwtDecoder(@Value("${security.jwt.secret}") String secret) {
        SecretKey key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(key).build();
    }

    /**
     * Configure une politique CORS permissive pour l'environnement de développement.
     *
     * @return une instance de {@link CorsConfigurationSource} appliquée à toutes les routes
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        var config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "*"));
        config.setExposedHeaders(List.of("Location", "Set-Cookie"));
        config.setAllowCredentials(true);

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
