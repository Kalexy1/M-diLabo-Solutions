package com.medilabo.patientui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Contrôleur de gestion des routes d’accès et de redirection
 * pour le microservice <strong>Patient UI</strong>.
 *
 * IMPORTANT :
 * - Le Gateway proxyfie /ui/** vers patient-ui en retirant le préfixe /ui.
 * - Donc les mappings internes ici NE doivent PAS commencer par /ui.
 * - En revanche, quand on renvoie une redirection au navigateur, on vise /ui/...,
 *   afin que le trafic repasse par la Gateway.
 */
@Controller
public class AuthController {

    /**
     * Page "Accès refusé" pour l'UI.
     * Mapping INTERNE sans /ui, car la Gateway appellera /access-denied.
     * Le template attendu est access-denied.html.
     */
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }
}
