package com.medilabo.noteservice.config;

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
 * Configuration de la sécurité du microservice NoteService.
 *
 * <p>Cette configuration active :</p>
 * <ul>
 *   <li>un mode stateless pour les sessions,</li>
 *   <li>la désactivation du CSRF,</li>
 *   <li>l'authentification obligatoire sur les endpoints {@code /api/**},</li>
 *   <li>un Resource Server JWT pour la validation des tokens.</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configure la chaîne de filtres de sécurité.
     *
     * @param http l'objet {@link HttpSecurity} à configurer
     * @return la chaîne de filtres {@link SecurityFilterChain} appliquée à l'application
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
     * Déclare un {@link JwtDecoder} capable de valider les JWT signés avec une clé HMAC.
     *
     * @param secret la clé secrète utilisée pour la signature des JWT
     * @return un décodeur JWT configuré avec la clé donnée
     */
    @Bean
    public JwtDecoder jwtDecoder(@Value("${security.jwt.secret}") String secret) {
        SecretKey key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(key).build();
    }
}
