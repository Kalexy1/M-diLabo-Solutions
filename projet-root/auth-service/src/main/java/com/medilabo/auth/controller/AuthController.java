package com.medilabo.auth.controller;

import com.medilabo.auth.model.AppUser;
import com.medilabo.auth.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * Application: com.medilabo.auth.controller
 * <p>
 * Classe <strong>AuthController</strong>.
 * <br/>
 * Rôle: Gère les opérations d'authentification et d'inscription des utilisateurs.
 * </p>
 */
@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    /**
     * Constructeur AuthController.
     *
     * @param userService service de gestion des utilisateurs.
     */
    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * showLoginForm: Affiche le formulaire de connexion.
     *
     * @return nom de la vue login.
     */
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    /**
     * showRegistrationForm: Affiche le formulaire d'inscription.
     *
     * @param model modèle pour la vue.
     * @return nom de la vue register.
     */
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new AppUser());
        return "register";
    }

    /**
     * registerUser: Traite l'inscription d'un nouvel utilisateur.
     *
     * @param user   utilisateur à enregistrer.
     * @param result résultats de validation.
     * @param model  modèle pour la vue.
     * @return redirection ou vue en cas d'erreur.
     */
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") AppUser user,
                               BindingResult result,
                               Model model) {

        if (result.hasErrors()) {
            return "register";
        }

        if (userService.userExists(user.getUsername())) {
            model.addAttribute("error", "Nom d'utilisateur déjà pris");
            return "register";
        }

        String role = user.getRole().toUpperCase();
        if (!role.startsWith("ROLE_")) {
            role = "ROLE_" + role;
        }
        user.setRole(role);

        userService.saveUser(user);
        return "redirect:/auth/login";
    }
}
