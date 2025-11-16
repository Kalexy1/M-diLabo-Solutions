package com.medilabo.patientui.web;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * Utilitaire pour extraire le jeton JWT depuis la requête HTTP.
 * Priorité :
 *   1) Header Authorization: Bearer <token>
 *   2) Cookie (par défaut "JWT_TOKEN")
 */
public final class JwtCookieUtil {

    private static final Logger log = LoggerFactory.getLogger(JwtCookieUtil.class);
    public static final String DEFAULT_COOKIE_NAME = "JWT_TOKEN";
    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private JwtCookieUtil() {}

    public static String extractJwt(HttpServletRequest request) {
        return extractOptional(request).orElse(null);
    }

    public static String extractJwt(HttpServletRequest request, String cookieName) {
        return extractOptional(request, cookieName).orElse(null);
    }

    public static Optional<String> extractOptional(HttpServletRequest request) {
        return extractOptional(request, DEFAULT_COOKIE_NAME);
    }

    public static Optional<String> extractOptional(HttpServletRequest request, String cookieName) {
        String fromAuth = extractFromAuthorizationHeader(request);
        if (fromAuth != null) return Optional.of(fromAuth);

        String fromCookie = extractFromCookie(request, cookieName);
        return Optional.ofNullable(fromCookie);
    }

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

    private static String cleanToken(String input) {
        if (input == null) return "";
        String s = input.trim();

        if ((s.startsWith("\"") && s.endsWith("\"")) || (s.startsWith("'") && s.endsWith("'"))) {
            s = s.substring(1, s.length() - 1).trim();
        }

        try {
            s = URLDecoder.decode(s, StandardCharsets.UTF_8);
        } catch (Exception ignored) {
        }

        return s.trim();
    }
}
