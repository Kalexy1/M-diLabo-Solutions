package com.medilabo.auth.config;

import com.medilabo.auth.security.JwtIssuer;
import jakarta.servlet.http.Cookie;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuration de la sécurité pour le microservice {@code auth-service}.
 *
 * <p>
 * Cette classe définit les règles de sécurité et la gestion de l’authentification.
 * Elle utilise Spring Security avec un formulaire de login personnalisé
 * et génère un JWT pour chaque utilisateur authentifié.
 * </p>
 *
 * <h3>Fonctionnalités principales</h3>
 * <ul>
 *   <li>Autorise l’accès public à certaines routes : {@code /auth/register}, {@code /auth/login},
 *       {@code /access-denied}, ressources statiques.</li>
 *   <li>Exige une authentification pour toutes les autres routes.</li>
 *   <li>Après une connexion réussie :
 *     <ul>
 *       <li>Un JWT signé est généré via {@link JwtIssuer}.</li>
 *       <li>Le JWT est placé dans un cookie HttpOnly {@code ACCESS_TOKEN} (valide 1h).</li>
 *       <li>L’utilisateur est redirigé vers {@code /ui/patients} via le gateway.</li>
 *     </ul>
 *   </li>
 *   <li>En cas d’échec de connexion, redirige vers {@code /auth/login?error}.</li>
 *   <li>À la déconnexion, le cookie {@code ACCESS_TOKEN} est supprimé et
 *       l’utilisateur est redirigé vers {@code /auth/login?logout}.</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    /**
     * Composant responsable de la génération des JWT.
     */
    private final JwtIssuer jwtIssuer;

    /**
     * Constructeur qui injecte le générateur de JWT.
     *
     * @param jwtIssuer service permettant d’émettre des JWT signés
     */
    public SecurityConfig(JwtIssuer jwtIssuer) {
        this.jwtIssuer = jwtIssuer;
    }

    /**
     * Chaîne de filtres de sécurité HTTP.
     *
     * <p>
     * Définit les règles d’accès, la gestion du formulaire de login et
     * le traitement personnalisé après authentification ou déconnexion.
     * </p>
     *
     * @param http objet {@link HttpSecurity} fourni par Spring
     * @return la configuration complète de la sécurité
     * @throws Exception si un problème survient lors de la configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/auth/register", "/auth/login", "/access-denied",
                    "/logout", "/css/**", "/js/**", "/images/**"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/auth/login")
                .loginProcessingUrl("/auth/login")
                .successHandler((req, res, authentication) -> {
                    String token = jwtIssuer.issue(authentication.getName(),
                            authentication.getAuthorities().stream().toList());

                    Cookie cookie = new Cookie("ACCESS_TOKEN", token);
                    cookie.setHttpOnly(true);
                    cookie.setPath("/");
                    cookie.setMaxAge(3600); // 1h

                    res.addCookie(cookie);
                    res.sendRedirect("/ui/patients");
                })
                .failureUrl("/auth/login?error")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .addLogoutHandler((req, res, auth) -> {
                    Cookie cookie = new Cookie("ACCESS_TOKEN", "");
                    cookie.setHttpOnly(true);
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    res.addCookie(cookie);
                })
                .logoutSuccessUrl("/auth/login?logout")
                .permitAll()
            );
        return http.build();
    }

    /**
     * Bean {@link PasswordEncoder} basé sur l’algorithme BCrypt.
     *
     * @return un encodeur de mots de passe sécurisé
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
