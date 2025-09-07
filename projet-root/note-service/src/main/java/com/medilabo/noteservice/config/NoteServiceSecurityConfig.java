// note-service/src/main/java/com/medilabo/noteservice/config/SecurityConfig.java
package com.medilabo.noteservice.config;

import javax.crypto.spec.SecretKeySpec; // ✅ correct
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
 * Application: com.medilabo.noteservice.config
 * <p>
 * Classe <strong>SecurityConfig</strong>.
 * <br/>
 * Rôle : Configure <em>note-service</em> en tant que <b>Resource Server JWT</b>.
 * </p>
 *
 * <h3>Règles d’accès</h3>
 * <ul>
 *   <li>CSRF désactivé (API REST).</li>
 *   <li><code>/actuator/**</code> : accès libre (supervision).</li>
 *   <li><code>GET /notes/**</code> : rôles <code>ROLE_PRATICIEN</code> ou <code>ROLE_ORGANISATEUR</code>.</li>
 *   <li><code>POST|PUT|DELETE /notes/**</code> : rôle <code>ROLE_PRATICIEN</code>.</li>
 *   <li>Toutes les autres requêtes nécessitent un JWT valide.</li>
 * </ul>
 *
 * <p>
 * Le JWT est propagé par le Gateway via l’en-tête
 * <code>Authorization: Bearer &lt;token&gt;</code>.
 * Le secret HMAC est injecté via <code>JWT_SECRET</code>.
 * </p>
 */
@Configuration
@EnableMethodSecurity
public class NoteServiceSecurityConfig {

  /**
   * Chaîne de filtres Spring Security (Resource Server JWT).
   */
  @Bean
  public SecurityFilterChain api(HttpSecurity http) throws Exception {
    return http
      .csrf(csrf -> csrf.disable())
      .authorizeHttpRequests(auth -> auth
        .requestMatchers("/actuator/**").permitAll()
        .requestMatchers(HttpMethod.GET, "/notes/**").hasAnyRole("PRATICIEN","ORGANISATEUR")
        .requestMatchers(HttpMethod.POST, "/notes/**").hasRole("PRATICIEN")
        .requestMatchers(HttpMethod.PUT,  "/notes/**").hasRole("PRATICIEN")
        .requestMatchers(HttpMethod.DELETE,"/notes/**").hasRole("PRATICIEN")
        .anyRequest().authenticated()
      )
      .oauth2ResourceServer(oauth -> oauth.jwt())
      .build();
  }

  /**
   * Décodeur JWT HS256 basé sur le secret partagé avec auth-service.
   *
   * @param secret valeur de {@code JWT_SECRET}
   */
  @Bean
  public JwtDecoder jwtDecoder(@Value("${JWT_SECRET}") String secret) {
    var key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    return NimbusJwtDecoder.withSecretKey(key).build();
  }
}
