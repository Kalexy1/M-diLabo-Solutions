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

@Component
@Order(-100)
public class CookieToAuthHeaderFilter implements Filter {

    private static final String COOKIE_NAME = "JWT_TOKEN";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpReq = (HttpServletRequest) request;
        HttpServletResponse httpRes = (HttpServletResponse) response;

        final String path = httpReq.getRequestURI();
        final String token = extractJwtCookie(httpReq);

        // DEBUG: trace la décision du filtre
        if ("/ui/patients".equals(path) || path.startsWith("/ui") || path.startsWith("/api")) {
            String cookieState = token == null ? "absent" : ("présent, len=" + token.length());
            System.out.println("[CookieToAuthHeaderFilter] path=" + path + " | JWT cookie " + cookieState);
        }

        // Public : /auth/**
        if (path.startsWith("/auth")) {
            chain.doFilter(httpReq, httpRes);
            return;
        }

        // Injecte Authorization pour /ui/** et /api/** si cookie présent
        if ((path.startsWith("/ui") || path.startsWith("/api")) && token != null && !token.isBlank()) {
            HttpServletRequest wrapped = new HttpServletRequestWrapperWithAuth(httpReq, token);
            chain.doFilter(wrapped, httpRes);
            return;
        }

        // Si pas de token sur /ui/** : redirection vers login
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

    private static class HttpServletRequestWrapperWithAuth extends jakarta.servlet.http.HttpServletRequestWrapper {
        private final String token;

        public HttpServletRequestWrapperWithAuth(HttpServletRequest request, String token) {
            super(request);
            this.token = token;
        }

        @Override
        public String getHeader(String name) {
            if ("Authorization".equalsIgnoreCase(name)) {
                return "Bearer " + token;
            }
            return super.getHeader(name);
        }

        @Override
        public Enumeration<String> getHeaders(String name) {
            if ("Authorization".equalsIgnoreCase(name)) {
                return Collections.enumeration(Collections.singleton("Bearer " + token));
            }
            return super.getHeaders(name);
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            var names = Collections.list(super.getHeaderNames());
            if (!names.contains("Authorization")) names.add("Authorization");
            return Collections.enumeration(names);
        }
    }
}
