package com.medilabo.riskassessment.config;

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
 * Configuration de la sécurité pour le microservice risk-assessment-service.
 *
 * <p>Cette configuration :</p>
 * <ul>
 *   <li>désactive la protection CSRF,</li>
 *   <li>met en place des sessions stateless,</li>
 *   <li>ouvre l’accès à {@code /actuator/**},</li>
 *   <li>protège les endpoints {@code /api/**} par JWT,</li>
 *   <li>et configure un Resource Server OAuth2 basé sur des jetons JWT signés.</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configure la chaîne de filtres Spring Security.
     *
     * @param http objet {@link HttpSecurity} à configurer
     * @return la {@link SecurityFilterChain} résultante
     * @throws Exception en cas d’erreur de configuration
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
     * Crée un décodeur JWT basé sur une clé secrète HMAC-SHA256.
     *
     * @param secret clé secrète utilisée pour la signature et la vérification des JWT
     * @return une instance de {@link JwtDecoder} configurée avec la clé fournie
     */
    @Bean
    public JwtDecoder jwtDecoder(@Value("${security.jwt.secret}") String secret) {
        SecretKey key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(key).build();
    }
}
