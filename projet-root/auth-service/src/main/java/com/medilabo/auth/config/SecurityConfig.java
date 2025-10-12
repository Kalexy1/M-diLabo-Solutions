package com.medilabo.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.http.HttpMethod;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
      // ✅ désactiver CSRF pour simplifier les formulaires
      .csrf(csrf -> csrf.disable())

      .authorizeHttpRequests(auth -> auth
        // ✅ autoriser explicitement les POST des formulaires
        .requestMatchers(HttpMethod.POST, "/auth/login", "/auth/register", "/auth/logout").permitAll()
        // ✅ et tout /auth/** en GET
        .requestMatchers("/auth/**", "/login", "/register").permitAll()
        // ✅ fichiers statiques
        .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**", "/actuator/**").permitAll()
        .anyRequest().authenticated()
      )

      // on n’utilise pas le formLogin de Spring (géré par AuthController)
      .formLogin(form -> form.disable())
      .logout(logout -> logout.disable())

      // non authentifié -> rediriger vers /auth/login
      .exceptionHandling(ex -> ex.authenticationEntryPoint((req, res, e) -> res.sendRedirect("/auth/login")));

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
