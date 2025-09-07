package com.medilabo.patientservice.config;

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
 * Application: com.medilabo.patientservice.config
 * <p>
 * Classe <strong>SecurityConfig</strong>.
 * <br/>
 * Rôle : Configure la sécurité pour le microservice <em>patient-service</em>
 * en tant que <b>Resource Server JWT</b>.
 * </p>
 *
 * <h3>Principes de configuration</h3>
 * <ul>
 *   <li><b>CSRF désactivé</b> car il s’agit d’une API REST sans formulaires HTML.</li>
 *   <li><b>Endpoints Actuator</b> exposés sans restriction pour la supervision.</li>
 *   <li><b>Accès restreints</b> sur les endpoints <code>/patients/**</code> :
 *     <ul>
 *       <li><code>GET</code> → réservé aux rôles <code>ROLE_PRATICIEN</code> ou <code>ROLE_ORGANISATEUR</code>.</li>
 *       <li><code>POST</code>, <code>PUT</code>, <code>DELETE</code> → réservés au rôle <code>ROLE_ORGANISATEUR</code>.</li>
 *     </ul>
 *   </li>
 *   <li><b>Toutes les autres requêtes</b> nécessitent un JWT valide.</li>
 *   <li><b>Les rôles</b> doivent être stockés avec le préfixe <code>ROLE_</code> en base de données.</li>
 * </ul>
 */
@Configuration
@EnableMethodSecurity
public class PatientServiceSecurityConfig {

    /**
     * Configure la chaîne de filtres Spring Security pour le microservice patient.
     *
     * <p>
     * Cette configuration :
     * <ul>
     *   <li>Désactive CSRF.</li>
     *   <li>Définit les règles d’accès par HTTP method et path.</li>
     *   <li>Active l’utilisation d’un Resource Server JWT pour valider les tokens
     *       signés par l’auth-service.</li>
     * </ul>
     * </p>
     *
     * @param http objet {@link HttpSecurity} fourni par Spring Security
     * @return une instance configurée de {@link SecurityFilterChain}
     * @throws Exception si une erreur survient lors de la configuration
     */
    @Bean
    public SecurityFilterChain api(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/patients/**")
                    .hasAnyRole("PRATICIEN", "ORGANISATEUR")
                .requestMatchers(HttpMethod.POST, "/patients/**").hasRole("ORGANISATEUR")
                .requestMatchers(HttpMethod.PUT,  "/patients/**").hasRole("ORGANISATEUR")
                .requestMatchers(HttpMethod.DELETE, "/patients/**").hasRole("ORGANISATEUR")
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth -> oauth.jwt())
            .build();
    }

    /**
     * Fournit un décodeur JWT basé sur l’algorithme HMAC-SHA256.
     *
     * <p>
     * Le secret est injecté via la propriété/variable d’environnement
     * <code>JWT_SECRET</code>, qui doit être identique à celui utilisé
     * par l’auth-service pour signer les tokens.
     * </p>
     *
     * @param secret la valeur du secret HMAC, injectée depuis la configuration
     * @return une instance de {@link JwtDecoder} configurée pour HS256
     */
    @Bean
    public JwtDecoder jwtDecoder(@Value("${JWT_SECRET}") String secret) {
        var key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(key).build();
    }
}
