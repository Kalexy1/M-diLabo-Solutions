package com.medilabo.gatewayservice.config;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.util.UriComponentsBuilder;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    /* ================= JWT ================= */

    @Bean
    public ReactiveJwtDecoder jwtDecoder(@Value("${JWT_SECRET}") String secret) {
        var key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return NimbusReactiveJwtDecoder.withSecretKey(key).build();
    }

    @Bean
    public ReactiveJwtAuthenticationConverterAdapter jwtAuthenticationConverter() {
        var delegate = new JwtAuthenticationConverter();
        delegate.setJwtGrantedAuthoritiesConverter(jwt -> {
            Object raw = jwt.getClaims().get("roles");
            if (raw instanceof Collection<?> coll) {
                return coll.stream()
                        .map(String::valueOf)
                        .filter(s -> !s.isBlank())
                        .map(s -> s.startsWith("ROLE_") ? s : "ROLE_" + s)
                        .map(SimpleGrantedAuthority::new)
                        .map(GrantedAuthority.class::cast)
                        .toList();
            }
            return List.<GrantedAuthority>of();
        });
        return new ReactiveJwtAuthenticationConverterAdapter(delegate);
    }

    /* ============ CHAIN 0: PUBLIC (/auth/**, /login, /register) ============ */
    @Bean
    @Order(-10)
    public SecurityWebFilterChain publicChain(ServerHttpSecurity http) {
        return http
            .securityMatcher(ServerWebExchangeMatchers.pathMatchers("/auth/**", "/login", "/register"))
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
            .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
            .authorizeExchange(ex -> ex
                // Autoriser explicitement toutes les méthodes nécessaires
                .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .pathMatchers(HttpMethod.GET,  "/auth/**", "/login", "/register").permitAll()
                .pathMatchers(HttpMethod.POST, "/auth/**", "/login", "/register").permitAll()
                .anyExchange().permitAll()
            )
            // Pas d’oauth2ResourceServer ici
            .build();
    }

    /* ================= CHAIN 1: UI ================= */
    @Bean
    @Order(0)
    public SecurityWebFilterChain uiChain(ServerHttpSecurity http,
                                          ReactiveJwtAuthenticationConverterAdapter jwtConv) {
        return http
            .securityMatcher(ServerWebExchangeMatchers.pathMatchers("/ui/**"))
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
            .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
            .authorizeExchange(ex -> ex
                .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .pathMatchers("/ui/**").hasAnyRole("ORGANISATEUR", "PRATICIEN")
                .anyExchange().denyAll()
            )
            .oauth2ResourceServer(oauth -> oauth.jwt(j -> j.jwtAuthenticationConverter(jwtConv)))
            .exceptionHandling(eh -> eh
                // Non authentifié → 303 vers /auth/login?redirect=<original>
                .authenticationEntryPoint((exchange, ex) -> {
                    var uri = exchange.getRequest().getURI();
                    String rawPath  = uri.getRawPath();
                    String rawQuery = uri.getRawQuery();
                    String original = (rawQuery == null || rawQuery.isBlank())
                            ? rawPath : rawPath + "?" + rawQuery;

                    URI loginWithRedirect = UriComponentsBuilder
                            .fromPath("/auth/login")
                            .queryParam("redirect", original)
                            .build(true).toUri();

                    exchange.getResponse().setStatusCode(HttpStatus.SEE_OTHER);
                    exchange.getResponse().getHeaders().setLocation(loginWithRedirect);
                    return exchange.getResponse().setComplete();
                })
                // Rôle insuffisant → 303 vers /ui/access-denied
                .accessDeniedHandler((exchange, ex) -> {
                    exchange.getResponse().setStatusCode(HttpStatus.SEE_OTHER);
                    exchange.getResponse().getHeaders().setLocation(URI.create("/ui/access-denied"));
                    return exchange.getResponse().setComplete();
                })
            )
            .build();
    }

    /* ================= CHAIN 2: API ================= */
    @Bean
    @Order(1)
    public SecurityWebFilterChain apiChain(ServerHttpSecurity http,
                                           ReactiveJwtAuthenticationConverterAdapter jwtConv) {
        return http
            .securityMatcher(ServerWebExchangeMatchers.pathMatchers("/api/**"))
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
            .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
            .authorizeExchange(ex -> ex
                .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // Spécifiques d'abord
                .pathMatchers("/api/notes/**").hasRole("PRATICIEN")
                .pathMatchers("/api/risk/**").hasRole("PRATICIEN")
                .pathMatchers("/api/patients/**").hasAnyRole("ORGANISATEUR", "PRATICIEN")
                // Générique
                .pathMatchers("/api/**").hasAnyRole("ORGANISATEUR", "PRATICIEN")
                .anyExchange().denyAll()
            )
            .oauth2ResourceServer(oauth -> oauth.jwt(j -> j.jwtAuthenticationConverter(jwtConv)))
            .exceptionHandling(eh -> eh
                // Non authentifié → 401
                .authenticationEntryPoint((exchange, ex) -> {
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                })
                // Rôle insuffisant → 403
                .accessDeniedHandler((exchange, ex) -> {
                    exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                    return exchange.getResponse().setComplete();
                })
            )
            .build();
    }

    /* ============== CHAIN 99: catch-all (optionnel) ============== */
    @Bean
    @Order(99)
    public SecurityWebFilterChain denyAll(ServerHttpSecurity http) {
        return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
            .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
            .authorizeExchange(ex -> ex.anyExchange().denyAll())
            .build();
    }
}
