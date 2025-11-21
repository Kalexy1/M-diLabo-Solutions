package com.medilabo.patientui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Contrôleur gérant les routes d'accès et les pages d'erreur
 * pour le microservice <strong>Patient UI</strong>.
 *
 * <p>
 * Les routes internes ne doivent pas commencer par <code>/ui</code>, car ce préfixe
 * est ajouté par le Gateway lors du routage. En revanche, les redirections renvoyées
 * au navigateur doivent viser des URLs commençant par <code>/ui</code> afin de repasser
 * correctement par le Gateway.
 * </p>
 */
@Controller
public class AuthController {

    /**
     * Affiche la page d'erreur "Accès refusé".
     *
     * <p>Cette route est appelée directement par le Gateway sous
     * <code>/access-denied</code>, et renvoie le template
     * <code>access-denied.html</code>.</p>
     *
     * @return le nom de la vue Thymeleaf à afficher
     */
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }
}
