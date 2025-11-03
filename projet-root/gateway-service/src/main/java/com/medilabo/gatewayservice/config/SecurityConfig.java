package com.medilabo.gatewayservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    // Autorise le CORS de manière explicite (adapte origins si besoin)
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration cfg = new CorsConfiguration();
        // ✅ Mets ici les origines qui appellent ta gateway (localhost front, domaine de prod, etc.)
        cfg.setAllowedOrigins(List.of(
            "http://localhost:3000",
            "http://localhost:4200",
            "https://app.medilabo.example" // exemple
        ));
        cfg.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        cfg.setAllowedHeaders(List.of("Authorization","Content-Type","X-Requested-With","Accept","Origin"));
        cfg.setExposedHeaders(List.of("Location")); // si tu relies sur Location, ETag, etc.
        cfg.setAllowCredentials(true); // important si tu utilises des cookies/sessions

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return new CorsWebFilter(source);
    }

    @Bean
    public SecurityWebFilterChain singleChain(ServerHttpSecurity http) {
        return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            // ❌ ne pas désactiver CORS ; on garde cors() actif
            .cors(c -> {}) 
            .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
            .formLogin(ServerHttpSecurity.FormLoginSpec::disable)

            .authorizeExchange(ex -> ex
                // Préflight CORS
                .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // Public
                .pathMatchers("/auth/**", "/login", "/register").permitAll()
                .pathMatchers("/css/**", "/js/**", "/images/**", "/webjars/**", "/favicon.ico").permitAll()
                .pathMatchers("/actuator/health", "/actuator/info").permitAll()
                .pathMatchers("/.well-known/**").permitAll() // ✅ évite du bruit inutile

                // Protégé
                .pathMatchers("/ui/**").authenticated()
                .pathMatchers("/api/patients/**", "/api/notes/**", "/api/risk/**").authenticated()

                // Le reste
                .anyExchange().authenticated()
            )

            // Optionnel : clarifier 401 vs 403 pour le debug
            .exceptionHandling(e -> e
            	    .authenticationEntryPoint((swe, ex) -> {
            	        swe.getResponse().setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
            	        return swe.getResponse().setComplete();
            	    })
            	    .accessDeniedHandler((swe, ex) -> {
            	        swe.getResponse().setStatusCode(org.springframework.http.HttpStatus.FORBIDDEN);
            	        return swe.getResponse().setComplete();
            	    })
            	)

            .build();
    }
}
