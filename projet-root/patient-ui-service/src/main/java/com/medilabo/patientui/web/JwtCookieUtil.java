package com.medilabo.patientui.web;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * Utilitaire permettant d’extraire le jeton JWT à partir d’une requête HTTP.
 *
 * <p>La recherche du token s’effectue dans l’ordre suivant :</p>
 * <ol>
 *     <li>Header <code>Authorization: Bearer &lt;token&gt;</code></li>
 *     <li>Cookie nommé <code>JWT_TOKEN</code> (ou un nom personnalisé)</li>
 * </ol>
 *
 * <p>
 * Les valeurs récupérées sont automatiquement nettoyées (suppression de guillemets,
 * décodage URL éventuel, retrait du préfixe <code>Bearer</code> si présent).
 * </p>
 */
public final class JwtCookieUtil {

    /** Logger interne pour les traces de diagnostic. */
    private static final Logger log = LoggerFactory.getLogger(JwtCookieUtil.class);

    /** Nom par défaut du cookie contenant le token JWT. */
    public static final String DEFAULT_COOKIE_NAME = "JWT_TOKEN";

    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    /** Constructeur privé – classe utilitaire non instanciable. */
    private JwtCookieUtil() {}

    /**
     * Extrait le JWT de la requête HTTP (header ou cookie).
     *
     * @param request requête HTTP source
     * @return le jeton JWT, ou {@code null} si absent
     */
    public static String extractJwt(HttpServletRequest request) {
        return extractOptional(request).orElse(null);
    }

    /**
     * Extrait le JWT depuis un cookie spécifique.
     *
     * @param request requête HTTP source
     * @param cookieName nom du cookie
     * @return le jeton JWT, ou {@code null} si absent
     */
    public static String extractJwt(HttpServletRequest request, String cookieName) {
        return extractOptional(request, cookieName).orElse(null);
    }

    /**
     * Extrait le JWT sous forme optionnelle avec recherche par ordre
     * (Authorization → cookie par défaut).
     *
     * @param request requête HTTP source
     * @return un {@link Optional} contenant le jeton si trouvé
     */
    public static Optional<String> extractOptional(HttpServletRequest request) {
        return extractOptional(request, DEFAULT_COOKIE_NAME);
    }

    /**
     * Extrait le JWT sous forme optionnelle (header, puis cookie donné).
     *
     * @param request requête HTTP source
     * @param cookieName nom du cookie contenant le JWT
     * @return un {@link Optional} contenant le jeton si trouvé
     */
    public static Optional<String> extractOptional(HttpServletRequest request, String cookieName) {
        String fromAuth = extractFromAuthorizationHeader(request);
        if (fromAuth != null) return Optional.of(fromAuth);

        String fromCookie = extractFromCookie(request, cookieName);
        return Optional.ofNullable(fromCookie);
    }

    /**
     * Tente d’extraire le token depuis le header Authorization.
     *
     * @param request requête HTTP
     * @return jeton extrait, ou {@code null}
     */
    private static String extractFromAuthorizationHeader(HttpServletRequest request) {
        String h = request.getHeader(AUTH_HEADER);
        if (h == null || h.isBlank()) {
            log.debug("Aucun header Authorization dans la requête");
            return null;
        }
        if (h.regionMatches(true, 0, BEARER_PREFIX, 0, BEARER_PREFIX.length())) {
            String token = cleanToken(h.substring(BEARER_PREFIX.length()));
            if (!token.isEmpty()) return token;
            log.debug("Header Authorization présent mais sans jeton Bearer");
            return null;
        }
        log.debug("Header Authorization présent mais sans schéma Bearer");
        return null;
    }

    /**
     * Tente d’extraire le token depuis un cookie.
     *
     * @param request requête HTTP
     * @param cookieName nom du cookie contenant le JWT
     * @return jeton extrait, ou {@code null}
     */
    private static String extractFromCookie(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) {
            log.debug("Aucun cookie trouvé dans la requête");
            return null;
        }
        for (Cookie c : cookies) {
            if (cookieName.equals(c.getName())) {
                String raw = c.getValue();
                if (raw == null || raw.isBlank()) {
                    log.debug("Le cookie {} est vide ou nul", cookieName);
                    return null;
                }
                String val = raw;
                if (val.regionMatches(true, 0, BEARER_PREFIX, 0, BEARER_PREFIX.length())) {
                    val = val.substring(BEARER_PREFIX.length());
                }
                String token = cleanToken(val);
                if (token.isEmpty()) {
                    log.debug("Le cookie {} ne contient pas de jeton exploitable", cookieName);
                    return null;
                }
                return token;
            }
        }
        log.debug("Cookie {} introuvable", cookieName);
        return null;
    }

    /**
     * Nettoie une chaîne supposée contenir un token :
     * <ul>
     *     <li>trim</li>
     *     <li>suppression de guillemets éventuels</li>
     *     <li>décodage URL</li>
     * </ul>
     *
     * @param input chaîne brute
     * @return token nettoyé (ou chaîne vide)
     */
    private static String cleanToken(String input) {
        if (input == null) return "";
        String s = input.trim();

        if ((s.startsWith("\"") && s.endsWith("\"")) || (s.startsWith("'") && s.endsWith("'"))) {
            s = s.substring(1, s.length() - 1).trim();
        }

        try {
            s = URLDecoder.decode(s, StandardCharsets.UTF_8);
        } catch (Exception ignored) {}

        return s.trim();
    }
}
