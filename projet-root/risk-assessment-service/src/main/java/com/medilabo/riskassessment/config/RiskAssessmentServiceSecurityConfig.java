package com.medilabo.riskassessment.config;

import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import java.nio.charset.StandardCharsets;

/**
 * Application: com.medilabo.riskassessment.config
 * <p>
 * Classe <strong>SecurityConfig</strong>.
 * <br/>
 * Rôle : Configure la sécurité pour le microservice <em>risk-assessment-service</em>
 * en tant que <b>Resource Server JWT</b>.
 * </p>
 *
 * <h3>Principes</h3>
 * <ul>
 *   <li>CSRF désactivé (API REST).</li>
 *   <li>Endpoints Actuator ouverts pour la supervision.</li>
 *   <li>Accès à <code>GET /risk/**</code> restreint au rôle <code>ROLE_PRATICIEN</code>.</li>
 *   <li>Toutes les autres requêtes nécessitent un JWT valide.</li>
 *   <li>Le secret HMAC est injecté via la propriété/variable d’env. <code>JWT_SECRET</code>.</li>
 * </ul>
 *
 * <p>
 * Le Gateway dépose le header <code>Authorization: Bearer &lt;token&gt;</code>
 * grâce au filtre Cookie→Authorization. Les rôles doivent être stockés en base
 * avec le préfixe <code>ROLE_</code> (ex. <code>ROLE_PRATICIEN</code>).
 * </p>
 */
@Configuration
@EnableMethodSecurity
public class RiskAssessmentServiceSecurityConfig {

  /**
   * Définit la chaîne de filtres de sécurité HTTP (Resource Server JWT).
   *
   * @param http l’instance {@link HttpSecurity}
   * @return la {@link SecurityFilterChain} configurée
   * @throws Exception si une erreur survient
   */
  @Bean
  public SecurityFilterChain api(HttpSecurity http) throws Exception {
    return http
      .csrf(csrf -> csrf.disable())
      .authorizeHttpRequests(auth -> auth
        .requestMatchers("/actuator/**").permitAll()
        .requestMatchers(HttpMethod.GET, "/risk/**").hasRole("PRATICIEN")
        .anyRequest().authenticated()
      )
      .oauth2ResourceServer(oauth -> oauth.jwt())
      .build();
  }

  /**
   * Décodeur JWT (HMAC-SHA256) basé sur le secret partagé avec l’auth-service.
   *
   * @param secret valeur de la propriété/variable d’environnement {@code JWT_SECRET}
   * @return un {@link JwtDecoder} HMAC
   */
  @Bean
  public JwtDecoder jwtDecoder(@Value("${JWT_SECRET}") String secret) {
    var key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    return NimbusJwtDecoder.withSecretKey(key).build();
  }
}
