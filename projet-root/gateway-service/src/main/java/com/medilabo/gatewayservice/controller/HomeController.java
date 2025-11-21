package com.medilabo.gatewayservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Contrôleur redirigeant les utilisateurs vers la page de connexion
 * lorsqu'ils accèdent à la racine de l'application.
 */
@Controller
public class HomeController {

    /**
     * Redirige les requêtes adressées à la racine ({@code "/"})
     * vers la page de connexion de l'application.
     *
     * @return une redirection HTTP vers {@code /auth/login}
     */
    @GetMapping("/")
    public String redirectToLogin() {
        return "redirect:/auth/login";
    }
}
