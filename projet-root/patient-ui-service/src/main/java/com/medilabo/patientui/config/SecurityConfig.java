package com.medilabo.patientui.config;

import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

/**
 * Configuration de sécurité du service UI.
 * <p>
 * Les ressources publiques sont en accès libre tandis que les routes sous
 * {@code /ui/**} nécessitent un JWT valide. La protection CSRF est activée
 * pour sécuriser les formulaires.
 * </p>
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    /**
     * Configure la chaîne de filtres Spring Security pour l’UI.
     * <p>
     * Active CSRF avec stockage du jeton en cookie, configure CORS, définit
     * les règles d’autorisation (accès public à certaines routes, authentification
     * requise pour {@code /ui/**}) et enregistre les redirections en cas d’accès
     * non authentifié ou refusé.
     * </p>
     *
     * @param http l’instance {@link HttpSecurity} à configurer
     * @return la {@link SecurityFilterChain} configurée
     * @throws Exception en cas d’erreur de configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
            .cors(Customizer.withDefaults())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/", "/login", "/auth/login").permitAll()
                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**", "/favicon.ico").permitAll()
                .requestMatchers("/ui/access-denied").permitAll()
                .requestMatchers("/ui/**").authenticated()
                .anyRequest().denyAll()
            )
            .oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter())))
            .exceptionHandling(e -> e
                .authenticationEntryPoint((req, res, ex) -> {
                    res.setStatus(303);
                    res.setHeader("Location", "/auth/login");
                })
                .accessDeniedHandler((req, res, ex) -> {
                    res.setStatus(303);
                    res.setHeader("Location", "/ui/access-denied");
                })
            )
            .build();
    }

    /**
     * Convertisseur d’authentification JWT qui extrait les rôles depuis le claim
     * {@code roles} et applique le préfixe {@code ROLE_} attendu par Spring Security.
     *
     * @return un {@link Converter} de {@link Jwt} vers {@link AbstractAuthenticationToken}
     */
    @Bean
    public Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthConverter() {
        var rolesConv = new JwtGrantedAuthoritiesConverter();
        rolesConv.setAuthoritiesClaimName("roles");
        rolesConv.setAuthorityPrefix("ROLE_");

        var conv = new JwtAuthenticationConverter();
        conv.setJwtGrantedAuthoritiesConverter(rolesConv);
        return conv;
    }

    /**
     * Décodeur JWT HMAC (HS256) basé sur un secret partagé.
     *
     * @param secret la clé secrète utilisée pour valider la signature des JWT
     * @return un {@link JwtDecoder} configuré avec la clé HMAC
     */
    @Bean
    public JwtDecoder jwtDecoder(@Value("${jwt.secret}") String secret) {
        SecretKey key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(key).build();
    }
}
