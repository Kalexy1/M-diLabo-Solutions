package com.medilabo.patientservice.config;

import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuration de la sécurité pour le microservice <strong>patient-service</strong>.
 * <p>
 * Règles principales :
 * <ul>
 *   <li>Les endpoints <code>/actuator/**</code> sont publics.</li>
 *   <li>Lecture des patients (<code>GET /api/patients/**</code>) autorisée aux rôles
 *       <code>ORGANISATEUR</code> et <code>PRATICIEN</code>.</li>
 *   <li>Écriture (<code>POST</code>, <code>PUT</code>, <code>DELETE</code> sur <code>/api/patients/**</code>)
 *       réservée au rôle <code>ORGANISATEUR</code>.</li>
 *   <li>Tout le reste est refusé.</li>
 * </ul>
 * L’API est protégée en tant que <em>resource server</em> OAuth2 avec des JWT HMAC.
 * Les rôles sont extraits du claim <code>roles</code> et préfixés en <code>ROLE_*</code>.
 * </p>
 */
@Configuration
@EnableMethodSecurity
public class PatientServiceSecurityConfig {

    /**
     * Déclare la chaîne de filtres Spring Security.
     * <p>
     * Désactive CSRF, définit les règles d'autorisation par méthode HTTP
     * et active la validation JWT via le convertisseur d’autorisations.
     * </p>
     *
     * @param http l’instance {@link HttpSecurity} à configurer
     * @param jwtAuthConverter le convertisseur d’authentification JWT (extraction des rôles)
     * @return la chaîne de filtres de sécurité configurée
     * @throws Exception en cas d’erreur de configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtAuthenticationConverter jwtAuthConverter) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/patients/**").hasAnyRole("ORGANISATEUR","PRATICIEN")
                .requestMatchers(HttpMethod.POST,   "/api/patients/**").hasRole("ORGANISATEUR")
                .requestMatchers(HttpMethod.PUT,    "/api/patients/**").hasRole("ORGANISATEUR")
                .requestMatchers(HttpMethod.DELETE, "/api/patients/**").hasRole("ORGANISATEUR")
                .anyRequest().denyAll()
            )
            .oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter)))
            .build();
    }

    /**
     * Crée un décodeur JWT HMAC (HS256) à partir d’un secret.
     * <p>
     * Le secret est lu depuis la propriété <code>jwt.secret</code> ou,
     * à défaut, depuis la variable d’environnement <code>JWT_SECRET</code>.
     * </p>
     *
     * @param secret la clé secrète utilisée pour valider les signatures JWT
     * @return un {@link JwtDecoder} basé sur une clé HMAC
     */
    @Bean
    public JwtDecoder jwtDecoder(@Value("${jwt.secret:${JWT_SECRET}}") String secret) {
        SecretKey key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(key).build();
    }

    /**
     * Configure le convertisseur d’authentification JWT.
     * <p>
     * Les autorités sont extraites du claim <code>roles</code> et
     * préfixées par <code>ROLE_</code> pour correspondre à la convention Spring Security.
     * </p>
     *
     * @return un {@link JwtAuthenticationConverter} configuré pour la gestion des rôles
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        var roles = new JwtGrantedAuthoritiesConverter();
        roles.setAuthoritiesClaimName("roles");
        roles.setAuthorityPrefix("ROLE_");

        var conv = new JwtAuthenticationConverter();
        conv.setJwtGrantedAuthoritiesConverter(roles);
        return conv;
    }
}
