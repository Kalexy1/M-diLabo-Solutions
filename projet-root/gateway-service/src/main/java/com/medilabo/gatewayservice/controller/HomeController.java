package com.medilabo.gatewayservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Contrôleur principal redirigeant les utilisateurs vers la page de connexion
 * lorsqu'ils accèdent à la racine de l'application.
 */
@Controller
public class HomeController {

    /**
     * Redirige la requête de la racine du site ("/") vers la page de connexion.
     *
     * @return une redirection vers {@code /auth/login}
     */
    @GetMapping("/")
    public String redirectToLogin() {
        return "redirect:/auth/login";
    }
}
