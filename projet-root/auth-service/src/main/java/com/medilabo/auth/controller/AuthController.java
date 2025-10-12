package com.medilabo.auth.controller;

import com.medilabo.auth.model.AppUser;
import com.medilabo.auth.model.UserRole;
import com.medilabo.auth.security.JwtIssuer;
import com.medilabo.auth.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

/**
 * Contrôleur d'authentification gérant l'affichage des formulaires,
 * la connexion, l'inscription et la déconnexion des utilisateurs.
 * <p>
 * Les opérations émettent un JWT stocké dans un cookie HTTP-only afin
 * d'authentifier l'utilisateur côté client.
 * </p>
 */
@Controller
@RequestMapping("/auth")
public class AuthController {

    /**
     * Service de gestion des utilisateurs.
     */
    private final UserService userService;

    /**
     * Composant responsable de l'émission des tokens JWT.
     */
    private final JwtIssuer jwtIssuer;

    /**
     * Nom du cookie contenant le JWT.
     * <p>Valeur par défaut : {@code JWT_TOKEN}.</p>
     */
    @Value("${security.jwt.cookie.name:JWT_TOKEN}")
    private String jwtCookieName;

    /**
     * Indique si le cookie JWT doit être marqué {@code Secure}.
     * <p>Valeur par défaut : {@code false}.</p>
     */
    @Value("${security.jwt.cookie.secure:false}")
    private boolean jwtCookieSecure;

    /**
     * Politique SameSite appliquée au cookie JWT.
     * <p>Valeur par défaut : {@code Lax}.</p>
     */
    @Value("${security.jwt.cookie.samesite:Lax}")
    private String jwtCookieSameSite;

    /**
     * Durée de vie du JWT en secondes.
     * <p>Valeur par défaut : 43&nbsp;200 (12h).</p>
     */
    @Value("${security.jwt.ttl-seconds:43200}")
    private long ttlSeconds;

    /**
     * Crée une instance du contrôleur d'authentification.
     *
     * @param userService le service de gestion des utilisateurs
     * @param jwtIssuer   l'émetteur de tokens JWT
     */
    public AuthController(UserService userService, JwtIssuer jwtIssuer) {
        this.userService = userService;
        this.jwtIssuer = jwtIssuer;
    }

    /**
     * Affiche le formulaire de connexion.
     *
     * @param error message d'erreur éventuel à afficher
     * @param model le modèle de la vue
     * @return le nom de la vue du formulaire de connexion
     */
    @GetMapping("/login")
    public String showLoginForm(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) model.addAttribute("error", "Identifiants invalides");
        return "login";
    }

    /**
     * Traite la soumission du formulaire de connexion.
     * <p>
     * En cas de succès : émet un JWT, le place dans un cookie HTTP-only
     * et redirige vers l'URL relative fournie (ou un chemin par défaut).
     * En cas d'échec : redirige vers la page de connexion avec un indicateur d'erreur.
     * </p>
     *
     * @param username le nom d'utilisateur
     * @param password le mot de passe
     * @param redirect chemin relatif de redirection (optionnel)
     * @param response la réponse HTTP utilisée pour ajouter le cookie
     * @return une redirection vers la destination appropriée
     */
    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        @RequestParam(value = "redirect", required = false) String redirect,
                        HttpServletResponse response) {

        String u = (username == null) ? null : username.trim();
        if (!userService.validateCredentials(u, password)) {
            return "redirect:/auth/login?error";
        }

        Optional<AppUser> opt = userService.findByUsername(u);
        if (opt.isEmpty()) {
            return "redirect:/auth/login?error";
        }
        AppUser user = opt.get();

        List<GrantedAuthority> authorities = List.of(
            new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );

        String token = jwtIssuer.issue(user.getUsername(), authorities);
        addJwtCookie(response, token);

        String target = normalizeRedirect(redirect, "/ui/patients");
        return "redirect:" + target;
    }

    /**
     * Affiche le formulaire d'inscription.
     *
     * @param model le modèle de la vue
     * @return le nom de la vue du formulaire d'inscription
     */
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new AppUser());
        model.addAttribute("roles", UserRole.values());
        return "register";
    }

    /**
     * Traite la soumission du formulaire d'inscription.
     * <p>
     * En cas de succès : crée l'utilisateur, émet un JWT,
     * le place dans un cookie HTTP-only et redirige vers l'URL relative fournie
     * (ou un chemin par défaut). En cas d'erreur de validation, réaffiche le formulaire.
     * Si l'utilisateur existe déjà, redirige vers le formulaire avec un code d'erreur.
     * </p>
     *
     * @param formUser     l'utilisateur saisi et validé
     * @param bindingResult le résultat de la validation
     * @param redirect     chemin relatif de redirection (optionnel)
     * @param response     la réponse HTTP utilisée pour ajouter le cookie
     * @return une redirection en cas de succès ou le nom de la vue en cas d'erreur
     */
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") AppUser formUser,
                           BindingResult bindingResult,
                           @RequestParam(value = "redirect", required = false) String redirect,
                           HttpServletResponse response) {

        if (bindingResult.hasErrors()) {
            return "register";
        }

        if (formUser.getRole() == null) {
            formUser.setRole(UserRole.ORGANISATEUR);
        }

        try {
            AppUser created = userService.register(formUser);

            List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + created.getRole().name())
            );
            String token = jwtIssuer.issue(created.getUsername(), authorities);
            addJwtCookie(response, token);

            String target = normalizeRedirect(redirect, "/ui/patients");
            return "redirect:" + target;

        } catch (IllegalArgumentException ex) {
            return "redirect:/auth/register?error=exists";
        }
    }

    /**
     * Déconnecte l'utilisateur en invalidant le cookie JWT.
     *
     * @param response la réponse HTTP utilisée pour supprimer le cookie
     * @param redirect chemin relatif de redirection (optionnel)
     * @return une redirection vers la page de connexion (ou une URL relative fournie)
     */
    @PostMapping("/logout")
    public String logout(HttpServletResponse response,
                         @RequestParam(value = "redirect", required = false) String redirect) {
        clearJwtCookie(response);
        String target = normalizeRedirect(redirect, "/auth/login?logout");
        return "redirect:" + target;
    }

    /**
     * Ajoute un cookie HTTP-only contenant le JWT à la réponse.
     *
     * @param response la réponse HTTP
     * @param token    le token JWT à stocker
     */
    private void addJwtCookie(HttpServletResponse response, String token) {
        ResponseCookie cookie = ResponseCookie.from(jwtCookieName, token)
            .httpOnly(true)
            .secure(jwtCookieSecure)
            .sameSite(jwtCookieSameSite)
            .path("/")
            .maxAge(Duration.ofSeconds(ttlSeconds))
            .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    /**
     * Supprime le cookie JWT en le remplaçant par un cookie expiré.
     *
     * @param response la réponse HTTP
     */
    private void clearJwtCookie(HttpServletResponse response) {
        ResponseCookie delete = ResponseCookie.from(jwtCookieName, "")
            .httpOnly(true)
            .secure(jwtCookieSecure)
            .sameSite(jwtCookieSameSite)
            .path("/")
            .maxAge(Duration.ZERO)
            .build();
        response.addHeader(HttpHeaders.SET_COOKIE, delete.toString());
    }

    /**
     * Normalise une destination de redirection en n'autorisant que des chemins relatifs commençant par {@code /}.
     * <p>
     * Si {@code redirect} est nul, vide ou ne commence pas par {@code /}, le chemin par défaut
     * est utilisé (et forcé à être relatif).
     * </p>
     *
     * @param redirect    la valeur de redirection fournie par le client (optionnelle)
     * @param defaultPath le chemin par défaut à utiliser si la redirection est invalide
     * @return un chemin relatif commençant par {@code /}
     */
    private String normalizeRedirect(String redirect, String defaultPath) {
        if (redirect != null && !redirect.isBlank() && redirect.startsWith("/")) {
            return redirect;
        }
        return defaultPath.startsWith("/") ? defaultPath : ("/" + defaultPath);
    }
}
