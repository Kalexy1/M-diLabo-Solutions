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

@Configuration
@EnableWebSecurity
@Profile("!test")
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           JwtAuthenticationConverter jwtAuthenticationConverter) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .formLogin(form -> form.disable())
            .logout(logout -> logout.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // ressources statiques éventuelles
                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter))
            );

        return http.build();
    }

    /**
     * Décodage du JWT (HS256) basé sur security.jwt.secret
     * (même secret que dans le gateway).
     */
    @Bean
    public JwtDecoder jwtDecoder(@Value("${security.jwt.secret}") String secret) {
        SecretKey key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(key).build();
    }

    /**
     * Convertit les claims du JWT en autorités Spring Security.
     * - claim "roles" : chaîne "ROLE_ROOT,ROLE_ORGANISATEUR"
     * - éventuellement claim "authorities" : liste ou chaîne
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(this::extractAuthorities);
        return converter;
    }

    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        Set<GrantedAuthority> authorities = new HashSet<>();

        Object rolesClaim = jwt.getClaims().get("roles");
        if (rolesClaim instanceof String s) {
            // ex: "ROLE_ROOT,ROLE_ORGANISATEUR"
            for (String part : s.split(",")) {
                String role = part.trim();
                if (!role.isEmpty()) {
                    // déjà préfixé ROLE_ par le gateway → on laisse tel quel
                    authorities.add(new SimpleGrantedAuthority(role));
                }
            }
        } else if (rolesClaim instanceof List<?> list) {
            // au cas où ce serait un tableau JSON ["ORGANISATEUR","PRATICIEN"]
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

        // Optionnel : support d’un claim "authorities"
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
