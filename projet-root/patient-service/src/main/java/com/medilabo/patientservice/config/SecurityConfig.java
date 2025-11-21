package com.medilabo.patientservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

/**
 * Configuration de la sécurité du microservice patient-service.
 *
 * <p>Cette configuration active :</p>
 * <ul>
 *   <li>la désactivation du CSRF,</li>
 *   <li>le mode stateless pour les sessions,</li>
 *   <li>l'accès libre à {@code /actuator/**},</li>
 *   <li>l'authentification obligatoire pour {@code /api/**},</li>
 *   <li>la validation des JWT via un Resource Server.</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configure la chaîne de filtres Spring Security.
     *
     * @param http l'objet {@link HttpSecurity} utilisé pour définir les règles de sécurité
     * @return la chaîne de filtres configurée
     * @throws Exception en cas d'erreur lors de la configuration
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
          .csrf(csrf -> csrf.disable())
          .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
          .authorizeHttpRequests(auth -> auth
              .requestMatchers("/actuator/**").permitAll()
              .requestMatchers("/api/**").authenticated()
              .anyRequest().permitAll()
          )
          .oauth2ResourceServer(oauth2 -> oauth2.jwt());
        return http.build();
    }

    /**
     * Crée un {@link JwtDecoder} capable de vérifier les JWT signés avec une clé secrète HMAC.
     *
     * @param secret la clé secrète définie dans la configuration de l'application
     * @return un décodeur JWT configuré pour la signature HMAC
     */
    @Bean
    public JwtDecoder jwtDecoder(@Value("${security.jwt.secret}") String secret) {
        SecretKey key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(key).build();
    }
}
