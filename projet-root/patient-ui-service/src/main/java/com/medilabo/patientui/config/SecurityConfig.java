package com.medilabo.patientui.config;

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
import org.springframework.security.oauth2.server.resource.authentication.*;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .authorizeHttpRequests(auth -> auth
                // Technique
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // PUBLICS (laisse passer la redirection et le pseudo-login côté UI/gateway)
                .requestMatchers("/", "/login", "/auth/login").permitAll()

                // Statique (Thymeleaf/Front)
                .requestMatchers(
                    "/css/**", "/js/**", "/images/**", "/webjars/**", "/favicon.ico"
                ).permitAll()

                // Page d’info "accès refusé" (réservée aux utilisateurs authentifiés)
                .requestMatchers("/ui/access-denied").authenticated()

                // Toute l’UI nécessite d’être connecté
                .requestMatchers("/ui/**").authenticated()

                // Par défaut
                .anyRequest().denyAll()
            )
            .oauth2ResourceServer(oauth -> oauth
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter()))
            )
            .exceptionHandling(e -> e
                .authenticationEntryPoint((req, res, ex) -> {
                    res.setStatus(303);               // 303 See Other
                    res.setHeader("Location", "/auth/login");
                })
                .accessDeniedHandler((req, res, ex) -> {
                    res.setStatus(303);
                    res.setHeader("Location", "/ui/access-denied");
                })
            )
            .build();
    }

    @Bean
    public Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthConverter() {
        var rolesConv = new JwtGrantedAuthoritiesConverter();
        rolesConv.setAuthoritiesClaimName("roles");
        rolesConv.setAuthorityPrefix("ROLE_");

        var conv = new JwtAuthenticationConverter();
        conv.setJwtGrantedAuthoritiesConverter(rolesConv);
        return conv;
    }

    @Bean
    public JwtDecoder jwtDecoder(@Value("${JWT_SECRET}") String secret) {
        var key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(key).build();
    }
}

