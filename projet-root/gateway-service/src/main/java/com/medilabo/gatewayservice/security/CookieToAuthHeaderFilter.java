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

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CookieToAuthHeaderFilter implements WebFilter {

    private static final Logger log = LoggerFactory.getLogger(CookieToAuthHeaderFilter.class);

    // Important : pas de "/" ni "/ui/**" ici (l'UI est protégé)
    private static final List<String> PUBLIC_PATTERNS = List.of(
        "/auth/**",
        "/login", "/register", "/logout",
        "/favicon.ico", "/error",
        "/css/**", "/js/**", "/images/**", "/assets/**",
        "/ui/css/**", "/ui/js/**", "/ui/images/**",
        "/actuator/**" // facultatif
    );

    private static final String JWT_COOKIE = "JWT_TOKEN";
    private final AntPathMatcher matcher = new AntPathMatcher();

    private boolean isPublic(String path) {
        for (String p : PUBLIC_PATTERNS) {
            if (matcher.match(p, path)) return true;
        }
        return false;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        var req = exchange.getRequest();
        var path = req.getURI().getPath();

        // 0) Préflight CORS : on laisse passer tel quel
        if (req.getMethod() == HttpMethod.OPTIONS) {
            return chain.filter(exchange);
        }

        // 1) Routes publiques : ne pas injecter
        if (isPublic(path)) {
            return chain.filter(exchange);
        }

        // 2) Ne pas écraser un Authorization déjà présent
        if (req.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            return chain.filter(exchange);
        }

        // 3) Injecter depuis le cookie JWT si présent
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

        // 4) Pas de cookie => laisser la sécu décider (401/303)
        return chain.filter(exchange);
    }
}
