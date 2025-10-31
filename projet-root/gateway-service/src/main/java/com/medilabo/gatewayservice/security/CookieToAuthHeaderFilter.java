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
 * Filtre WebFlux responsable de copier le token JWT stocké dans un cookie HTTP-only
 * vers l'en-tête {@code Authorization: Bearer <token>} si nécessaire.
 * <p>
 * Le filtre :
 * <ul>
 *   <li>Ignore les routes publiques et d'authentification.</li>
 *   <li>Laisse passer les requêtes préflight CORS (OPTIONS).</li>
 *   <li>Ne remplace pas un en-tête Authorization déjà existant.</li>
 *   <li>Injecte l'en-tête Authorization à partir du cookie JWT s'il est présent.</li>
 * </ul>
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CookieToAuthHeaderFilter implements WebFilter {

    private static final Logger log = LoggerFactory.getLogger(CookieToAuthHeaderFilter.class);
    private static final List<String> PUBLIC_PATTERNS = List.of(
        "/auth/**", "/ui/**",
        "/login", "/register", "/logout",
        "/favicon.ico", "/error",
        "/css/**", "/js/**", "/images/**", "/assets/**",
        "/ui/css/**", "/ui/js/**", "/ui/images/**",
        "/actuator/**"
    );
    private static final String JWT_COOKIE = "JWT_TOKEN";
    private final AntPathMatcher matcher = new AntPathMatcher();

    /**
     * Vérifie si le chemin correspond à une route publique.
     *
     * @param path chemin de la requête
     * @return {@code true} si le chemin est public, sinon {@code false}
     */
    private boolean isPublic(String path) {
        for (String p : PUBLIC_PATTERNS) {
            if (matcher.match(p, path)) return true;
        }
        return false;
    }

    /**
     * Filtre qui ajoute l'en-tête Authorization à partir du cookie JWT.
     *
     * @param exchange l'échange WebFlux
     * @param chain la chaîne de filtres
     * @return un {@link Mono} indiquant la poursuite du traitement
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        var req = exchange.getRequest();
        var path = req.getURI().getPath();

        if (path.startsWith("/auth/")
            || "/login".equals(path)
            || "/register".equals(path)
            || "/logout".equals(path)) {
            return chain.filter(exchange);
        }

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
            String token = jwtCookie.getValue();
            if (token != null && !token.isBlank()) {
                if (log.isDebugEnabled()) {
                    log.debug("Inject Authorization from cookie for path {}", path);
                }
                ServerHttpRequest mutated = req.mutate()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token.trim())
                    .build();
                return chain.filter(exchange.mutate().request(mutated).build());
            }
        }

        return chain.filter(exchange);
    }
}
