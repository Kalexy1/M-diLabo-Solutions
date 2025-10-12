package com.medilabo.gatewayservice.security;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * Filtre WebFlux qui recopie un JWT stocké dans un cookie HTTP-only
 * vers l'en-tête {@code Authorization: Bearer <token>} lorsqu'il est présent.
 * <p>
 * Le filtre s'applique avec la plus haute priorité, ignore les routes publiques,
 * ne modifie pas les requêtes préflight CORS et n'écrase jamais un en-tête
 * {@code Authorization} déjà défini.
 * </p>
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CookieToAuthHeaderFilter implements WebFilter {

    private static final Logger log = LoggerFactory.getLogger(CookieToAuthHeaderFilter.class);

    /**
     * Motifs d'URL publiques pour lesquelles aucune injection d'en-tête n'est effectuée.
     */
    private static final List<String> PUBLIC_PATTERNS = List.of(
        "/auth/**",
        "/login", "/register", "/logout",
        "/favicon.ico", "/error",
        "/css/**", "/js/**", "/images/**", "/assets/**",
        "/ui/css/**", "/ui/js/**", "/ui/images/**",
        "/actuator/**"
    );

    /**
     * Nom du cookie contenant le JWT.
     */
    private static final String JWT_COOKIE = "JWT_TOKEN";

    /**
     * Utilitaire de correspondance de chemins avec motifs Ant.
     */
    private final AntPathMatcher matcher = new AntPathMatcher();

    /**
     * Indique si un chemin correspond à l'une des routes publiques.
     *
     * @param path le chemin de la requête
     * @return {@code true} si le chemin est public, sinon {@code false}
     */
    private boolean isPublic(String path) {
        for (String p : PUBLIC_PATTERNS) {
            if (matcher.match(p, path)) return true;
        }
        return false;
    }

    /**
     * Applique la logique d'injection de l'en-tête {@code Authorization} à partir du cookie JWT.
     * <ul>
     *   <li>Autorise sans modification les requêtes {@code OPTIONS} (préflight CORS).</li>
     *   <li>N'agit pas sur les routes publiques.</li>
     *   <li>Ne remplace pas un en-tête {@code Authorization} existant.</li>
     *   <li>Si un cookie JWT est présent et non vide, injecte l'en-tête {@code Authorization} de type Bearer.</li>
     *   <li>À défaut, laisse la chaîne de filtres décider (pouvant mener à 401/303 selon la configuration).</li>
     * </ul>
     *
     * @param exchange l'échange serveur web
     * @param chain    la chaîne des filtres Web
     * @return un {@link Mono} représentant la poursuite du traitement
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        var req = exchange.getRequest();
        var path = req.getURI().getPath();

        if (req.getMethod() == HttpMethod.OPTIONS) {
            return chain.filter(exchange);
        }

        if (isPublic(path)) {
            return chain.filter(exchange);
        }

        if (req.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            return chain.filter(exchange);
        }

        MultiValueMap<String, HttpCookie> cookies = req.getCookies();
        HttpCookie jwtCookie = cookies.getFirst(JWT_COOKIE);
        if (jwtCookie != null) {
            String raw = jwtCookie.getValue();
            if (raw != null) {
                String token = raw.trim();
                if (!token.isEmpty()) {
                    if (log.isDebugEnabled()) {
                        log.debug("Inject Authorization from cookie for path {}", path);
                    }
                    ServerHttpRequest mutated = req.mutate()
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .build();
                    return chain.filter(exchange.mutate().request(mutated).build());
                }
            }
        }

        return chain.filter(exchange);
    }
}
