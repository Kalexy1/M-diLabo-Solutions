package com.medilabo.patientui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Application: com.medilabo.patientui.controller
 * <p>
 * Classe <strong>AuthController</strong>.
 * <br/>
 * Rôle : Gère les pages liées à l'authentification et aux erreurs d'accès
 * pour le microservice <em>patient-ui-service</em>.
 * </p>
 */
@Controller
public class AuthController {

    /**
     * Affiche la page "Accès refusé" lorsqu'un utilisateur
     * tente d'accéder à une ressource protégée sans les droits nécessaires.
     *
     * @return le nom de la vue Thymeleaf {@code access-denied}.
     */
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }
}
