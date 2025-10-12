package com.medilabo.auth.controller;

import com.medilabo.auth.model.AppUser;
import com.medilabo.auth.model.UserRole;
import com.medilabo.auth.security.JwtIssuer;
import com.medilabo.auth.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final JwtIssuer jwtIssuer;

    @Value("${security.jwt.cookie.name:JWT_TOKEN}")
    private String jwtCookieName;

    @Value("${security.jwt.cookie.secure:false}")
    private boolean jwtCookieSecure;

    @Value("${security.jwt.cookie.samesite:Lax}")
    private String jwtCookieSameSite;

    @Value("${security.jwt.ttl-seconds:43200}") // 12h
    private long ttlSeconds;

    public AuthController(UserService userService, JwtIssuer jwtIssuer) {
        this.userService = userService;
        this.jwtIssuer = jwtIssuer;
    }

    // ---------- LOGIN ----------
    @GetMapping("/login")
    public String showLoginForm(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) model.addAttribute("error", "Identifiants invalides");
        return "login"; // templates/login.html
    }

    @PostMapping("/login")
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

        List<GrantedAuthority> authorities = List.of(
            new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );

        String token = jwtIssuer.issue(user.getUsername(), authorities);
        addJwtCookie(response, token);

        // Redirection RELATIVE (pas d’URL absolue)
        String target = normalizeRedirect(redirect, "/ui/patients");
        return "redirect:" + target;
    }

    // ---------- REGISTER ----------
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new AppUser());
        model.addAttribute("roles", UserRole.values());
        return "register"; // templates/register.html
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") AppUser formUser,
                           BindingResult bindingResult,
                           @RequestParam(value = "redirect", required = false) String redirect,
                           HttpServletResponse response) {

        if (bindingResult.hasErrors()) {
            return "register";
        }

        if (formUser.getRole() == null) {
            formUser.setRole(UserRole.ORGANISATEUR);
        }

        try {
            AppUser created = userService.register(formUser);

            List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + created.getRole().name())
            );
            String token = jwtIssuer.issue(created.getUsername(), authorities);
            addJwtCookie(response, token);

            String target = normalizeRedirect(redirect, "/ui/patients");
            return "redirect:" + target;

        } catch (IllegalArgumentException ex) {
            return "redirect:/auth/register?error=exists";
        }
    }

    // ---------- LOGOUT ----------
    @PostMapping("/logout")
    public String logout(HttpServletResponse response,
                         @RequestParam(value = "redirect", required = false) String redirect) {
        clearJwtCookie(response);
        // Redirection RELATIVE
        String target = normalizeRedirect(redirect, "/auth/login?logout");
        return "redirect:" + target;
    }

    // ---------- Helpers ----------
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

    /** N’autorise que des redirections RELATIVES commençant par “/”. */
    private String normalizeRedirect(String redirect, String defaultPath) {
        if (redirect != null && !redirect.isBlank() && redirect.startsWith("/")) {
            return redirect;
        }
        return defaultPath.startsWith("/") ? defaultPath : ("/" + defaultPath);
    }
}
