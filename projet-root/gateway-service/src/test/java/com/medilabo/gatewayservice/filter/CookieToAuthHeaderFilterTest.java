package com.medilabo.gatewayservice.filter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockCookie;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class CookieToAuthHeaderFilterTest {

    private final CookieToAuthHeaderFilter filter = new CookieToAuthHeaderFilter();

    @Test
    void injects_authorization_from_cookie_on_protected_path() throws ServletException, IOException {
        // given: requête sur une route protégée (pas /auth/**) avec cookie JWT_TOKEN
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/api/patients");
        req.setCookies(new MockCookie("JWT_TOKEN", "abc.def.ghi"));
        MockHttpServletResponse res = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        // when
        filter.doFilter(req, res, chain);

        // then: le chain "voit" la requête mutée (via HttpServletRequestWrapper du filtre)
        HttpServletRequest forwarded = (HttpServletRequest) chain.getRequest();
        assertNotNull(forwarded, "la requête forwardée ne doit pas être nulle");
        assertEquals("Bearer abc.def.ghi",
                forwarded.getHeader("Authorization"),
                "Authorization doit être injecté depuis le cookie JWT_TOKEN");
    }

    @Test
    void does_not_inject_on_public_path() throws ServletException, IOException {
        // given: route publique /auth/**
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/auth/login");
        req.setCookies(new MockCookie("JWT_TOKEN", "abc.def"));
        MockHttpServletResponse res = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        // when
        filter.doFilter(req, res, chain);

        // then: sur /auth/**, aucune injection de l'en-tête Authorization
        HttpServletRequest forwarded = (HttpServletRequest) chain.getRequest();
        assertNotNull(forwarded);
        assertNull(forwarded.getHeader("Authorization"),
                "Authorization ne doit PAS être injecté sur /auth/**");
    }
}
