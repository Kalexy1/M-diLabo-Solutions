package com.medilabo.gatewayservice.controller;

import com.medilabo.gatewayservice.jwt.JwtIssuer;
import com.medilabo.gatewayservice.model.AppUser;
import com.medilabo.gatewayservice.model.UserRole;
import com.medilabo.gatewayservice.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Optional;

/**
 * Contrôleur gérant l'authentification des utilisateurs.
 *
 * <p>Ce contrôleur expose les endpoints de connexion, d'inscription et de
 * déconnexion sous le préfixe {@code /auth}. Il s'appuie sur {@link UserService}
 * pour la gestion des utilisateurs et sur {@link JwtIssuer} pour la génération
 * des jetons JWT stockés dans un cookie HTTP.</p>
 */
@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final JwtIssuer jwtIssuer;
    private final PasswordEncoder passwordEncoder;

    private final String cookieName;
    private final int ttlSeconds;
    private final String sameSite;
    private final boolean cookieSecure;

    /**
     * Construit le contrôleur d'authentification.
     *
     * @param userService   service de gestion des utilisateurs
     * @param jwtIssuer     composant responsable de l'émission des JWT
     * @param passwordEncoder encodeur de mots de passe (utilisé dans la couche service)
     * @param cookieName    nom du cookie JWT
     * @param ttlSeconds    durée de vie du JWT en secondes
     * @param sameSite      stratégie SameSite appliquée au cookie
     * @param cookieSecure  indique si le cookie doit être marqué sécurisé
     */
    @Autowired
    public AuthController(
            UserService userService,
            JwtIssuer jwtIssuer,
            PasswordEncoder passwordEncoder,
            @Value("${security.jwt.cookie.name:JWT_TOKEN}") String cookieName,
            @Value("${security.jwt.ttl-seconds:43200}") int ttlSeconds,
            @Value("${security.jwt.cookie.samesite:None}") String sameSite,
            @Value("${security.jwt.cookie.secure:false}") boolean cookieSecure
    ) {
        this.userService = userService;
        this.jwtIssuer = jwtIssuer;
        this.passwordEncoder = passwordEncoder;
        this.cookieName = cookieName;
        this.ttlSeconds = ttlSeconds;
        this.sameSite = sameSite;
        this.cookieSecure = cookieSecure;
    }

    /**
     * Affiche la page de connexion.
     *
     * @param redirect URL vers laquelle rediriger après succès éventuel de la connexion
     * @param model    modèle utilisé pour passer les attributs à la vue
     * @return le nom de la vue de connexion
     */
    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "redirect", required = false) String redirect, Model model) {
        model.addAttribute("redirect", redirect);
        return "login";
    }

    /**
     * Traite la soumission du formulaire de connexion.
     *
     * @param username identifiant de l'utilisateur
     * @param password mot de passe fourni
     * @param redirect URL de redirection après authentification
     * @param response réponse HTTP utilisée pour ajouter le cookie JWT
     * @return une redirection vers la page cible ou vers la page de login en cas d'erreur
     */
    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        @RequestParam(required = false) String redirect,
                        HttpServletResponse response) {

        if (!userService.validateCredentials(username, password)) {
            return "redirect:/auth/login?error";
        }

        AppUser user = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Utilisateur introuvable après validation"));

        String token = jwtIssuer.issue(user.getUsername(), user.getRole().name());
        addJwtCookie(response, token);

        String target = (redirect != null && redirect.startsWith("/")) ? redirect : "/ui/patients";
        return "redirect:" + target;
    }

    /**
     * Affiche le formulaire d'inscription.
     *
     * @param redirect URL vers laquelle rediriger après inscription
     * @param model    modèle utilisé pour injecter l'utilisateur et les rôles disponibles
     * @return le nom de la vue d'inscription
     */
    @GetMapping("/register")
    public String registerPage(@RequestParam(value = "redirect", required = false) String redirect, Model model) {
        model.addAttribute("user", new AppUser());
        model.addAttribute("roles", UserRole.values());
        model.addAttribute("redirect", redirect);
        return "register";
    }

    /**
     * Traite la soumission du formulaire d'inscription.
     *
     * @param username identifiant souhaité
     * @param password mot de passe en clair (l'encodage est géré dans {@link UserService})
     * @param role     rôle attribué au nouvel utilisateur
     * @param redirect URL de redirection après inscription
     * @param response réponse HTTP utilisée pour ajouter le cookie JWT
     * @return une redirection vers la page cible ou vers la page d'inscription en cas de conflit
     */
    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String password,
                           @RequestParam UserRole role,
                           @RequestParam(required = false) String redirect,
                           HttpServletResponse response) {

        Optional<AppUser> existing = userService.findByUsername(username);
        if (existing.isPresent()) {
            return "redirect:/auth/register?error=exists";
        }

        AppUser newUser = new AppUser();
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setRole(role);

        userService.register(newUser);

        String token = jwtIssuer.issue(newUser.getUsername(), newUser.getRole().name());
        addJwtCookie(response, token);

        String target = (redirect != null && redirect.startsWith("/")) ? redirect : "/ui/patients";
        return "redirect:" + target;
    }

    /**
     * Déconnecte l'utilisateur courant en expirant le cookie JWT.
     *
     * @param response réponse HTTP utilisée pour écraser le cookie
     * @return une redirection vers la page de login avec un indicateur de déconnexion
     */
    @PostMapping("/logout")
    public String logout(HttpServletResponse response) {
        expireJwtCookie(response);
        return "redirect:/auth/login?logout";
    }

    /**
     * Ajoute un cookie JWT à la réponse HTTP.
     *
     * @param response réponse HTTP à laquelle ajouter le cookie
     * @param token    jeton JWT à stocker dans le cookie
     */
    private void addJwtCookie(HttpServletResponse response, String token) {
        ResponseCookie rc = ResponseCookie.from(cookieName, token)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite(sameSite)
                .path("/")
                .maxAge(Duration.ofSeconds(ttlSeconds))
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, rc.toString());
    }

    /**
     * Expire le cookie JWT en le remplaçant par un cookie vide.
     *
     * @param response réponse HTTP à laquelle ajouter le cookie expiré
     */
    private void expireJwtCookie(HttpServletResponse response) {
        ResponseCookie rc = ResponseCookie.from(cookieName, "")
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite(sameSite)
                .path("/")
                .maxAge(Duration.ZERO)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, rc.toString());
    }
}
