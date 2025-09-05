package com.medilabo.auth.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Application: com.medilabo.auth.controller
 * <p>
 * Classe <strong>HomeController</strong>.
 * <br/>
 * Rôle: Redirige les utilisateurs vers les pages appropriées en fonction de leur authentification.
 * </p>
 */
@Controller
public class HomeController {

    /**
     * redirectToPatients: Redirige vers la page des patients si l'utilisateur est authentifié, sinon vers la page de connexion.
     *
     * @param authentication objet contenant les informations d'authentification de l'utilisateur.
     * @return redirection vers la page appropriée.
     */
    @GetMapping("/")
    public String redirectToPatients(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/patients";
        }
        return "redirect:/auth/login";
    }
}
