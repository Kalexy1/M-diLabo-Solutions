package com.medilabo.gatewayservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuration de la sécurité pour le service Gateway.
 * <p>
 * Cette classe centralise la gestion de la sécurité pour l'ensemble des microservices␊
 * en imposant l'authentification via la gateway. Les utilisateurs sont chargés depuis la␊
 * base de données grâce à {@code CustomUserDetailsService} et les pages de connexion et␊
 * d'inscription sont fournies par la gateway.
 */
@Configuration
public class SecurityConfig {
    /**
     * Définit la chaîne de filtres de sécurité HTTP.
     * <p>
     * Les pages de connexion et d'inscription sont accessibles sans authentification,
     * tandis que les routes proxifiées vers les microservices et les autres pages
     * nécessitent une authentification.
     * </p>
     *
     * @param http l'objet {@link HttpSecurity} configuré par Spring Security
     * @return un {@link SecurityFilterChain} configuré pour cette application
     * @throws Exception en cas d'erreur de configuration
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
		    .authorizeHttpRequests(auth -> auth
		        .requestMatchers("/css/**", "/js/**", "/images/**", "/register", "/login").permitAll()
		        .requestMatchers("/patients/**", "/notes/**","/risk/**").hasAnyRole("ORGANISATEUR", "PRATICIEN")
		        .anyRequest().authenticated()
		    )
		    .formLogin(form -> form
		        .loginPage("/login")
		        .defaultSuccessUrl("/patients", true)
		        .permitAll()
		    )
		    .logout(logout -> logout
		        .logoutSuccessUrl("/login?logout")
		        .permitAll()
		    )
		    .httpBasic();

        return http.build();
    }

    /**
     * Fournit un encodeur de mots de passe basé sur BCrypt pour sécuriser les mots de passe
     * des utilisateurs gérés par la gateway.
     *
     * @return une instance de {@link BCryptPasswordEncoder}
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}