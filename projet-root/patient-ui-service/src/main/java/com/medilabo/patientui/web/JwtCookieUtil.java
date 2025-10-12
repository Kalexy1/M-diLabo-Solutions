package com.medilabo.patientui.web;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

public final class JwtCookieUtil {

    private JwtCookieUtil() {}

    public static String extractJwt(HttpServletRequest request, String cookieName) {
        if (request.getCookies() == null) return null;
        for (Cookie c : request.getCookies()) {
            if (cookieName.equals(c.getName())) {
                String v = c.getValue();
                return (v != null && !v.isBlank()) ? v : null;
            }
        }
        return null;
    }
}
