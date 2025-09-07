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
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

/**
 * Application: com.medilabo.auth.config
 * <p>
 * Classe <strong>SecurityConfig</strong>.
 * <br/>
 * Rôle : Configure la sécurité pour le microservice <em>auth-service</em>.
 * </p>
 *
 * <h3>Fonctionnalités principales :</h3>
 * <ul>
 *   <li>Active la protection CSRF avec stockage du token dans un cookie
 *       (lisible côté client pour l’intégration avec Thymeleaf).</li>
 *   <li>Autorise l’accès public aux routes :
 *       <ul>
 *         <li><code>/auth/register</code> → inscription</li>
 *         <li><code>/auth/login</code> → formulaire de login</li>
 *         <li><code>/access-denied</code> → page d’accès refusé</li>
 *         <li><code>/logout</code> → déconnexion</li>
 *         <li>ressources statiques (<code>/css/**</code>, <code>/js/**</code>, <code>/images/**</code>)</li>
 *         <li>Actuator (<code>/actuator/**</code>)</li>
 *       </ul>
 *   </li>
 *   <li>Exige une authentification pour toutes les autres requêtes.</li>
 *   <li>Configure un formulaire de login personnalisé sur <code>/auth/login</code>.</li>
 *   <li>À la connexion réussie :
 *     <ul>
 *       <li>Un JWT est généré via {@link JwtIssuer}.</li>
 *       <li>Le JWT est stocké dans un cookie HttpOnly nommé <code>ACCESS_TOKEN</code> valable 1h.</li>
 *       <li>L’utilisateur est redirigé vers <code>/patients</code> (via le Gateway).</li>
 *     </ul>
 *   </li>
 *   <li>En cas d’échec → redirection vers <code>/auth/login?error</code>.</li>
 *   <li>À la déconnexion :
 *     <ul>
 *       <li>Le cookie <code>ACCESS_TOKEN</code> est supprimé.</li>
 *       <li>Redirection vers <code>/auth/login?logout</code>.</li>
 *     </ul>
 *   </li>
 *   <li>Page d’accès refusé définie sur <code>/access-denied</code>.</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    /** Service responsable de la génération des JWT. */
    private final JwtIssuer jwtIssuer;

    /**
     * Constructeur avec injection de dépendance.
     *
     * @param jwtIssuer service permettant de créer des JWT signés
     */
    public SecurityConfig(JwtIssuer jwtIssuer) {
        this.jwtIssuer = jwtIssuer;
    }

    /**
     * Définit la chaîne de filtres Spring Security pour l’application.
     *
     * <p>Points clés :</p>
     * <ul>
     *   <li>CSRF activé avec {@link CookieCsrfTokenRepository} (token envoyé dans un cookie).</li>
     *   <li>Autorisation des routes publiques.</li>
     *   <li>Formulaire de login configuré sur <code>/auth/login</code>.</li>
     *   <li>Gestion de la génération et de la suppression du cookie JWT.</li>
     *   <li>Page d’accès refusé personnalisée.</li>
     * </ul>
     *
     * @param http objet {@link HttpSecurity} configuré par Spring
     * @return la {@link SecurityFilterChain} résultante
     * @throws Exception en cas d’erreur de configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
          .csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
          .authorizeHttpRequests(auth -> auth
              .requestMatchers(
                  "/auth/register", "/auth/login", "/access-denied",
                  "/logout", "/css/**", "/js/**", "/images/**",
                  "/actuator/**"
              ).permitAll()
              .anyRequest().authenticated()
          )
          .formLogin(form -> form
              .loginPage("/auth/login")
              .loginProcessingUrl("/auth/login")
              .successHandler((req, res, authentication) -> {
                  String token = jwtIssuer.issue(
                      authentication.getName(),
                      authentication.getAuthorities().stream().toList()
                  );

                  Cookie cookie = new Cookie("ACCESS_TOKEN", token);
                  cookie.setHttpOnly(true);
                  cookie.setPath("/");
                  cookie.setMaxAge(3600);
                  res.addCookie(cookie);

                  res.sendRedirect("http://gateway-service:8080/patients");
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
          )
          .exceptionHandling(e -> e.accessDeniedPage("/access-denied"));

        return http.build();
    }

    /**
     * Fournit un encodeur de mots de passe utilisant BCrypt.
     *
     * @return une instance de {@link PasswordEncoder}
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
