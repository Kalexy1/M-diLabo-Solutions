package com.medilabo.gatewayservice.security;

import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

@Component
public class CookieToAuthHeaderFilterTest implements GlobalFilter, Ordered {

    private static final String COOKIE_NAME = "JWT";

    private static final Set<String> PUBLIC_PREFIXES = Set.of(
        "/auth/", "/access-denied", "/favicon.ico", "/webjars/", "/css/", "/js/", "/images/"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();

        if (request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            return chain.filter(exchange);
        }

        String path = request.getURI().getRawPath();
        if (isPublic(path)) {
            return chain.filter(exchange);
        }

        var cookies = request.getCookies().get(COOKIE_NAME);
        if (cookies == null || cookies.isEmpty()) {
            return chain.filter(exchange);
        }

        String token = sanitize(cookies.get(0).getValue());
        if (token.isEmpty()) {
            return chain.filter(exchange);
        }

        ServerHttpRequest mutated = request.mutate()
            .headers(h -> h.put(HttpHeaders.AUTHORIZATION, List.of("Bearer " + token)))
            .build();

        return chain.filter(exchange.mutate().request(mutated).build());
    }

    private boolean isPublic(String path) {
        if (path == null || path.isEmpty()) return true;
        for (String p : PUBLIC_PREFIXES) {
            if (path.equals(p) || path.startsWith(p)) return true;
        }
        return false;
    }

    private String sanitize(String raw) {
        if (raw == null) return "";
        String s = raw.trim();
        if (s.length() >= 2 &&
            ((s.startsWith("\"") && s.endsWith("\"")) ||
             (s.startsWith("'") && s.endsWith("'")))) {
            s = s.substring(1, s.length() - 1).trim();
        }
        return s;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
