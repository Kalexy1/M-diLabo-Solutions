package com.medilabo.gatewayservice.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Filtre servlet chargé de convertir un cookie JWT en en-tête {@code Authorization}.
 *
 * <p>Ce filtre intercepte les requêtes afin de :</p>
 * <ul>
 *   <li>extraire un jeton JWT stocké dans un cookie,</li>
 *   <li>injecter un en-tête {@code Authorization: Bearer &lt;token&gt;} pour les chemins
 *       commençant par {@code /ui} et {@code /api},</li>
 *   <li>rediriger vers la page de login si l'accès à {@code /ui/**} est tenté sans jeton.</li>
 * </ul>
 *
 * <p>Les endpoints sous {@code /auth/**} restent accessibles sans authentification.</p>
 */
@Component
@Order(-100)
public class CookieToAuthHeaderFilter implements Filter {

    private static final String COOKIE_NAME = "JWT_TOKEN";

    /**
     * Applique la logique de filtrage :
     * <ul>
     *   <li>laisse passer les requêtes {@code /auth/**} sans modification,</li>
     *   <li>ajoute un en-tête {@code Authorization} pour {@code /ui/**} et {@code /api/**}
     *       lorsqu'un cookie JWT est présent,</li>
     *   <li>redirige vers {@code /auth/login} pour les chemins {@code /ui/**} sans jeton,</li>
     *   <li>laisse passer les autres requêtes telles quelles.</li>
     * </ul>
     *
     * @param request  la requête entrante
     * @param response la réponse associée
     * @param chain    la chaîne de filtres à poursuivre
     * @throws IOException      en cas d'erreur d'entrée/sortie
     * @throws ServletException en cas d'erreur liée au traitement du filtre
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpReq = (HttpServletRequest) request;
        HttpServletResponse httpRes = (HttpServletResponse) response;

        final String path = httpReq.getRequestURI();
        final String token = extractJwtCookie(httpReq);

        if ("/ui/patients".equals(path) || path.startsWith("/ui") || path.startsWith("/api")) {
            String cookieState = token == null ? "absent" : ("présent, len=" + token.length());
            System.out.println("[CookieToAuthHeaderFilter] path=" + path + " | JWT cookie " + cookieState);
        }

        if (path.startsWith("/auth")) {
            chain.doFilter(httpReq, httpRes);
            return;
        }

        if ((path.startsWith("/ui") || path.startsWith("/api")) && token != null && !token.isBlank()) {
            HttpServletRequest wrapped = new HttpServletRequestWrapperWithAuth(httpReq, token);
            chain.doFilter(wrapped, httpRes);
            return;
        }

        if (path.startsWith("/ui") && (token == null || token.isBlank())) {
            String target = httpReq.getRequestURI();
            String qs = httpReq.getQueryString();
            if (qs != null && !qs.isBlank()) target += "?" + qs;
            String encoded = URLEncoder.encode(target, StandardCharsets.UTF_8);
            System.out.println("[CookieToAuthHeaderFilter] Pas de JWT, redirect -> /auth/login?redirect=" + encoded);
            httpRes.sendRedirect("/auth/login?redirect=" + encoded);
            return;
        }

        chain.doFilter(httpReq, httpRes);
    }

    /**
     * Extrait la valeur du cookie JWT à partir de la requête HTTP.
     *
     * @param req la requête contenant éventuellement le cookie JWT
     * @return la valeur du jeton ou {@code null} si le cookie est absent
     */
    private static String extractJwtCookie(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();
        if (cookies == null) return null;
        return Stream.of(cookies)
                .filter(Objects::nonNull)
                .filter(c -> COOKIE_NAME.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    /**
     * Wrapper de {@link HttpServletRequest} ajoutant un en-tête {@code Authorization}
     * construit à partir d'un jeton JWT.
     */
    private static class HttpServletRequestWrapperWithAuth extends jakarta.servlet.http.HttpServletRequestWrapper {
        private final String token;

        /**
         * Crée un wrapper de requête ajoutant un en-tête {@code Authorization}.
         *
         * @param request la requête originale
         * @param token   le jeton JWT à utiliser dans l'en-tête
         */
        public HttpServletRequestWrapperWithAuth(HttpServletRequest request, String token) {
            super(request);
            this.token = token;
        }

        /**
         * Retourne la valeur de l'en-tête demandé.
         * Si le nom est {@code Authorization}, renvoie {@code Bearer <token>}.
         *
         * @param name nom de l'en-tête
         * @return la valeur de l'en-tête
         */
        @Override
        public String getHeader(String name) {
            if ("Authorization".equalsIgnoreCase(name)) {
                return "Bearer " + token;
            }
            return super.getHeader(name);
        }

        /**
         * Retourne toutes les valeurs de l'en-tête demandé.
         * Si le nom est {@code Authorization}, renvoie une collection contenant {@code Bearer <token>}.
         *
         * @param name nom de l'en-tête
         * @return un {@link Enumeration} des valeurs de l'en-tête
         */
        @Override
        public Enumeration<String> getHeaders(String name) {
            if ("Authorization".equalsIgnoreCase(name)) {
                return Collections.enumeration(Collections.singleton("Bearer " + token));
            }
            return super.getHeaders(name);
        }

        /**
         * Retourne la liste des noms d'en-têtes disponibles, incluant toujours {@code Authorization}.
         *
         * @return un {@link Enumeration} des noms d'en-têtes
         */
        @Override
        public Enumeration<String> getHeaderNames() {
            var names = Collections.list(super.getHeaderNames());
            if (!names.contains("Authorization")) names.add("Authorization");
            return Collections.enumeration(names);
        }
    }
}
