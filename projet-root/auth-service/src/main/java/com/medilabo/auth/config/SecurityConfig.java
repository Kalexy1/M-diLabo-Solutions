package com.medilabo.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> {}) // ⬅️ Active CORS

            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**").permitAll()                 // tout /auth/** public
                .requestMatchers(HttpMethod.POST, "/auth/login", "/auth/register").permitAll()
                .requestMatchers("/", "/login", "/register", "/error").permitAll()
                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**", "/actuator/**").permitAll()
                .anyRequest().authenticated()
            )

            .formLogin(f -> f.disable())
            .logout(l -> l.disable())

            .exceptionHandling(ex ->
                ex.authenticationEntryPoint((req, res, e) -> {
                    res.setStatus(302);
                    res.setHeader("Location", "/auth/login");
                })
            );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        // Origin du navigateur (Gateway) et nom DNS docker de la gateway
        cfg.setAllowedOrigins(List.of("http://localhost:8080", "http://gateway-service:8080"));
        cfg.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setAllowCredentials(true);  // cookies JWT
        cfg.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
