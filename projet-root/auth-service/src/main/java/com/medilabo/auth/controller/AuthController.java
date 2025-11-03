package com.medilabo.auth.controller;

import com.medilabo.auth.model.AppUser;
import com.medilabo.auth.model.UserRole;
import com.medilabo.auth.security.JwtIssuer;
import com.medilabo.auth.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/auth")
@CrossOrigin(
    origins = { "http://localhost:8080", "http://gateway-service:8080" },
    allowCredentials = "true"
)
public class AuthController {

    private final UserService userService;
    private final JwtIssuer jwtIssuer;

    @Value("${security.jwt.cookie.name:JWT_TOKEN}")
    private String jwtCookieName;

    @Value("${security.jwt.cookie.secure:false}")
    private boolean jwtCookieSecure;

    @Value("${security.jwt.cookie.samesite:Lax}")
    private String jwtCookieSameSite;

    @Value("${security.jwt.ttl-seconds:43200}")
    private long ttlSeconds;

    public AuthController(UserService userService, JwtIssuer jwtIssuer) {
        this.userService = userService;
        this.jwtIssuer = jwtIssuer;
    }

    @GetMapping({"/login", "/register"})
    public String pages(Model model) {
        model.addAttribute("user", new AppUser());
        model.addAttribute("roles", UserRole.values());
        // Spring choisit login.html ou register.html selon l'URL grâce au ViewResolver
        return null;
    }

    @PostMapping(
        path = "/login",
        consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        @RequestParam(value = "redirect", required = false) String redirect,
                        HttpServletResponse response) {

        String u = (username == null) ? null : username.trim();
        if (!userService.validateCredentials(u, password)) {
            return "redirect:/auth/login?error";
        }

        Optional<AppUser> opt = userService.findByUsername(u);
        if (opt.isEmpty()) {
            return "redirect:/auth/login?error";
        }
        AppUser user = opt.get();

        List<GrantedAuthority> authorities =
            List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

        String token = jwtIssuer.issue(user.getUsername(), authorities);
        addJwtCookie(response, token);

        return "redirect:" + normalizeRedirect(redirect, "/ui/patients");
    }

    @PostMapping(
        path = "/register",
        consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    public String register(@RequestParam String username,
                           @RequestParam String password,
                           @RequestParam(required = false, defaultValue = "ORGANISATEUR") String role,
                           @RequestParam(value = "redirect", required = false) String redirect,
                           HttpServletResponse response) {

        // si l’utilisateur existe déjà, on continue comme un login souple
        AppUser created = userService.findByUsername(username.trim().toLowerCase())
                .orElseGet(() -> {
                    AppUser u = new AppUser();
                    u.setUsername(username.trim().toLowerCase());
                    u.setPassword(password);
                    u.setRole(UserRole.valueOf(role.trim().toUpperCase()));
                    return userService.register(u);
                });

        List<GrantedAuthority> authorities =
            List.of(new SimpleGrantedAuthority("ROLE_" + created.getRole().name()));

        String token = jwtIssuer.issue(created.getUsername(), authorities);
        addJwtCookie(response, token);

        return "redirect:" + normalizeRedirect(redirect, "/ui/patients");
    }

    @PostMapping("/logout")
    public String logout(HttpServletResponse response,
                         @RequestParam(value = "redirect", required = false) String redirect) {
        clearJwtCookie(response);
        return "redirect:" + normalizeRedirect(redirect, "/auth/login?logout");
    }

    /* ----------------- helpers ----------------- */

    private void addJwtCookie(HttpServletResponse response, String token) {
        ResponseCookie cookie = ResponseCookie.from(jwtCookieName, token)
            .httpOnly(true)
            .secure(jwtCookieSecure)
            .sameSite(jwtCookieSameSite)
            .path("/")
            .maxAge(Duration.ofSeconds(ttlSeconds))
            .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void clearJwtCookie(HttpServletResponse response) {
        ResponseCookie delete = ResponseCookie.from(jwtCookieName, "")
            .httpOnly(true)
            .secure(jwtCookieSecure)
            .sameSite(jwtCookieSameSite)
            .path("/")
            .maxAge(Duration.ZERO)
            .build();
        response.addHeader(HttpHeaders.SET_COOKIE, delete.toString());
    }

    private String normalizeRedirect(String redirect, String def) {
        if (redirect != null && !redirect.isBlank() && redirect.startsWith("/")) return redirect;
        return def.startsWith("/") ? def : ("/" + def);
    }
}
