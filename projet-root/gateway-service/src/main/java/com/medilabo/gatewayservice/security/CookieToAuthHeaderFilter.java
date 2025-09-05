package com.medilabo.gatewayservice.security;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * <strong>CookieToAuthHeaderFilter</strong>
 * <p>
 * Ce filtre Spring WebFlux est appliqué au niveau du Gateway.
 * Il intercepte les requêtes entrantes et, si un cookie {@code ACCESS_TOKEN} est présent,
 * il le transforme en en-tête HTTP {@code Authorization} avec le préfixe {@code Bearer }.
 * </p>
 *
 * <h3>Règles de fonctionnement</h3>
 * <ul>
 *   <li>Si l’en-tête {@code Authorization} est déjà présent → ne fait rien.</li>
 *   <li>Si le cookie {@code ACCESS_TOKEN} est absent ou vide → ne fait rien.</li>
 *   <li>Sinon → ajoute un en-tête {@code Authorization: Bearer &lt;token&gt;} à la requête.</li>
 * </ul>
 *
 * <h3>Utilité</h3>
 * <p>
 * Permet d’utiliser un JWT stocké dans un cookie HttpOnly (posé par l’auth-service après login),
 * tout en respectant les attentes de Spring Security qui lit le token dans le header.
 * </p>
 */
@Component
public class CookieToAuthHeaderFilter implements WebFilter {

    /** Nom du cookie contenant le JWT. */
    public static final String COOKIE_NAME = "ACCESS_TOKEN";

    /** Nom de l'en-tête standard d'authentification HTTP. */
    private static final String AUTH_HEADER = "Authorization";

    /** Préfixe attendu par la spec OAuth2 / Bearer Token. */
    private static final String BEARER_PREFIX = "Bearer ";

    /**
     * Filtre chaque requête entrante et ajoute un en-tête {@code Authorization}
     * si un cookie {@code ACCESS_TOKEN} valide est trouvé.
     *
     * @param exchange le contexte d’échange HTTP (contient requête et réponse)
     * @param chain la chaîne de filtres WebFlux
     * @return un {@link Mono} complété après traitement de la requête
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if (exchange.getRequest().getHeaders().containsKey(AUTH_HEADER)) {
            return chain.filter(exchange);
        }

        var cookie = exchange.getRequest().getCookies().getFirst(COOKIE_NAME);
        if (cookie == null) {
            return chain.filter(exchange);
        }

        String token = cookie.getValue();
        if (token == null || token.isBlank()) {
            return chain.filter(exchange);
        }

        ServerHttpRequest mutated = exchange.getRequest()
                .mutate()
                .header(AUTH_HEADER, BEARER_PREFIX + token)
                .build();

        return chain.filter(exchange.mutate().request(mutated).build());
    }
}
