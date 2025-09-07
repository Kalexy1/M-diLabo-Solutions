package com.medilabo.gatewayservice.security;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Application: gateway-service
 *
 * <p><strong>CookieToAuthHeaderFilter</strong></p>
 *
 * <p>
 * Filtre global de la Gateway qui transforme un cookie JWT {@code ACCESS_TOKEN}
 * en en-tête {@code Authorization: Bearer &lt;token&gt;} afin que les microservices
 * en aval puissent valider l’utilisateur via Spring Security Resource Server.
 * </p>
 *
 * <h3>Règles de fonctionnement</h3>
 * <ul>
 *   <li><b>Ne surcharge pas</b> un en-tête {@code Authorization} déjà présent.</li>
 *   <li>Ignore les requêtes CORS preflight ({@code OPTIONS}).</li>
 *   <li>Ne fait rien si le cookie {@code ACCESS_TOKEN} est absent ou vide.</li>
 *   <li><b>Bypass</b> complet des routes :
 *     <ul>
 *       <li>{@code /auth/login}</li>
 *       <li>{@code /auth/register}</li>
 *       <li>{@code /actuator/**} (supervision)</li>
 *     </ul>
 *   </li>
 *   <li>Sinon, ajoute un en-tête {@code Authorization: Bearer &lt;token&gt;}.</li>
 * </ul>
 *
 * <h3>Utilité</h3>
 * <p>
 * Cela permet de stocker le JWT en cookie HttpOnly (déposé par auth-service lors du login),
 * tout en garantissant que les appels internes entre microservices passent bien le token
 * dans le header attendu par Spring Security.
 * </p>
 */
@Component
public class CookieToAuthHeaderFilter implements GlobalFilter, Ordered {

    public static final String COOKIE_NAME = "ACCESS_TOKEN";
    private static final String AUTH_HEADER = HttpHeaders.AUTHORIZATION;
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (exchange.getRequest().getHeaders().containsKey(AUTH_HEADER)) {
            return chain.filter(exchange);
        }

        if (exchange.getRequest().getMethod() == HttpMethod.OPTIONS) {
            return chain.filter(exchange);
        }

        var cookie = exchange.getRequest().getCookies().getFirst(COOKIE_NAME);
        if (cookie == null || cookie.getValue() == null || cookie.getValue().isBlank()) {
            return chain.filter(exchange);
        }
        String token = cookie.getValue();

        String path = exchange.getRequest().getURI().getPath();
        if (path.startsWith("/auth/login")
                || path.startsWith("/auth/register")
                || path.startsWith("/actuator/")) {
            return chain.filter(exchange);
        }

        ServerHttpRequest mutated = exchange.getRequest()
            .mutate()
            .header(AUTH_HEADER, BEARER_PREFIX + token)
            .build();

        return chain.filter(exchange.mutate().request(mutated).build());
    }

    /**
     * Ordre négatif pour exécution très tôt dans la chaîne des filtres.
     */
    @Override
    public int getOrder() {
        return -100;
    }
}
