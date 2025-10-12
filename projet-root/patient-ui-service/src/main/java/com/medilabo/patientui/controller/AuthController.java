package com.medilabo.patientui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Contrôleur de gestion des routes d’accès et de redirection
 * pour le microservice <strong>Patient UI</strong>.
 * <p>
 * Ce contrôleur redirige la racine du site vers la liste des patients
 * et fournit une page dédiée en cas d’accès refusé.
 * </p>
 */
@Controller
public class AuthController {

    /**
     * Redirige la page d’accueil vers la section principale
     * de l’interface des patients.
     *
     * @return une redirection vers {@code /ui/patients}
     */
    @GetMapping("/")
    public String home() {
        return "redirect:/ui/patients";
    }

    /**
     * Affiche la page d’accès refusé.
     *
     * @return le nom du template Thymeleaf {@code access-denied}
     */
    @GetMapping("/ui/access-denied")
    public String accessDenied() {
        return "access-denied";
    }
}
