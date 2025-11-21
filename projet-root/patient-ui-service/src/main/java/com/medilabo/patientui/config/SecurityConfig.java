package com.medilabo.patientui.config;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuration de la sécurité pour le microservice patient-ui-service.
 *
 * <p>Cette configuration s'applique uniquement en dehors du profil {@code test}.
 * Elle active un mode stateless, désactive les mécanismes d'authentification
 * classiques (form-login, logout) et s'appuie sur un Resource Server JWT
 * pour authentifier les utilisateurs.</p>
 *
 * <p>Les rôles utilisateurs sont extraits du JWT fourni par le gateway.</p>
 */
@Configuration
@EnableWebSecurity
@Profile("!test")
public class SecurityConfig {

    /**
     * Configure la chaîne de filtres Spring Security.
     *
     * <ul>
     *   <li>Désactive CSRF, form-login et logout</li>
     *   <li>Active le mode stateless</li>
     *   <li>Protège toutes les routes sauf les ressources statiques</li>
     *   <li>Utilise un Resource Server JWT pour l'authentification</li>
     * </ul>
     *
     * @param http configuration HttpSecurity
     * @param jwtAuthenticationConverter convertisseur personnalisé des rôles
     * @return chaîne de filtres configurée
     * @throws Exception en cas d'erreur de configuration
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           JwtAuthenticationConverter jwtAuthenticationConverter) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .formLogin(form -> form.disable())
            .logout(logout -> logout.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter))
            );

        return http.build();
    }

    /**
     * Déclare un décodeur JWT utilisant l’algorithme HMAC-SHA256 basé sur la clé secrète.
     *
     * @param secret clé secrète commune au gateway et aux microservices
     * @return un {@link JwtDecoder} configuré pour valider les JWT
     */
    @Bean
    public JwtDecoder jwtDecoder(@Value("${security.jwt.secret}") String secret) {
        SecretKey key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(key).build();
    }

    /**
     * Convertisseur permettant de transformer les claims du JWT en autorités
     * Spring Security ({@link GrantedAuthority}).
     *
     * <p>Gère notamment :</p>
     * <ul>
     *   <li>le claim {@code roles} (format chaîne ou liste),</li>
     *   <li>le claim optionnel {@code authorities}.</li>
     * </ul>
     *
     * @return un {@link JwtAuthenticationConverter} configuré
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(this::extractAuthorities);
        return converter;
    }

    /**
     * Extrait les rôles et autorités contenus dans le JWT.
     *
     * <p>Supporte :</p>
     * <ul>
     *   <li>un claim {@code roles} sous forme de chaîne : "ROLE_ADMIN,ROLE_USER"</li>
     *   <li>un claim {@code roles} sous forme de liste : ["ADMIN","USER"]</li>
     *   <li>un claim optionnel {@code authorities}</li>
     * </ul>
     *
     * @param jwt JWT analysé
     * @return ensemble d'autorisations Spring Security
     */
    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        Set<GrantedAuthority> authorities = new HashSet<>();

        Object rolesClaim = jwt.getClaims().get("roles");
        if (rolesClaim instanceof String s) {
            for (String part : s.split(",")) {
                String role = part.trim();
                if (!role.isEmpty()) {
                    authorities.add(new SimpleGrantedAuthority(role));
                }
            }
        } else if (rolesClaim instanceof List<?> list) {
            for (Object o : list) {
                if (o != null) {
                    String role = o.toString().trim();
                    if (!role.isEmpty()) {
                        if (!role.startsWith("ROLE_")) {
                            role = "ROLE_" + role;
                        }
                        authorities.add(new SimpleGrantedAuthority(role));
                    }
                }
            }
        }

        Object authClaim = jwt.getClaims().get("authorities");
        if (authClaim instanceof String s) {
            for (String part : s.split(",")) {
                String val = part.trim();
                if (val.isEmpty()) continue;
                if (!val.startsWith("ROLE_")) {
                    val = "ROLE_" + val;
                }
                authorities.add(new SimpleGrantedAuthority(val));
            }
        } else if (authClaim instanceof List<?> list) {
            for (Object o : list) {
                if (o != null) {
                    String val = o.toString().trim();
                    if (val.isEmpty()) continue;
                    if (!val.startsWith("ROLE_")) {
                        val = "ROLE_" + val;
                    }
                    authorities.add(new SimpleGrantedAuthority(val));
                }
            }
        }

        return authorities;
    }
}
