package com.medilabo.patientui.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Utilitaire pour extraire le jeton JWT stocké dans les cookies HTTP.
 * <p>
 * Cette classe fournit une méthode statique pour récupérer la valeur d’un
 * cookie spécifique (généralement {@code JWT_TOKEN}) à partir d’une requête HTTP.
 * Si le cookie n’existe pas ou que sa valeur est vide, la méthode renvoie {@code null}.
 * </p>
 */
public final class JwtCookieUtil {

    /**
     * Logger pour le suivi des opérations de lecture des cookies.
     */
    private static final Logger log = LoggerFactory.getLogger(JwtCookieUtil.class);

    /**
     * Extrait la valeur du JWT depuis un cookie dans la requête HTTP.
     *
     * @param request    la requête HTTP contenant éventuellement les cookies
     * @param cookieName le nom du cookie à rechercher
     * @return la valeur du JWT si le cookie est trouvé et non vide,
     *         sinon {@code null}
     */
    public static String extractJwt(HttpServletRequest request, String cookieName) {
        if (request.getCookies() == null) {
            log.debug("Aucun cookie trouvé dans la requête");
            return null;
        }
        for (Cookie c : request.getCookies()) {
            if (cookieName.equals(c.getName())) {
                String v = c.getValue();
                if (v == null || v.isBlank()) {
                    log.debug("Le cookie {} est vide ou nul", cookieName);
                    return null;
                }
                return v;
            }
        }
        log.debug("Cookie {} introuvable", cookieName);
        return null;
    }
}
