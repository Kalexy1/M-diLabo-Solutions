package com.medilabo.gatewayservice.controller;

import com.medilabo.gatewayservice.jwt.JwtIssuer;
import com.medilabo.gatewayservice.model.AppUser;
import com.medilabo.gatewayservice.model.UserRole;
import com.medilabo.gatewayservice.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Optional;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final JwtIssuer jwtIssuer;
    private final PasswordEncoder passwordEncoder;

    private final String cookieName;
    private final int ttlSeconds;
    private final String sameSite;
    private final boolean cookieSecure;

    @Autowired
    public AuthController(
            UserService userService,
            JwtIssuer jwtIssuer,
            PasswordEncoder passwordEncoder,
            @Value("${security.jwt.cookie.name:JWT_TOKEN}") String cookieName,
            @Value("${security.jwt.ttl-seconds:43200}") int ttlSeconds,
            @Value("${security.jwt.cookie.samesite:None}") String sameSite,
            @Value("${security.jwt.cookie.secure:false}") boolean cookieSecure
    ) {
        this.userService = userService;
        this.jwtIssuer = jwtIssuer;
        this.passwordEncoder = passwordEncoder;
        this.cookieName = cookieName;
        this.ttlSeconds = ttlSeconds;
        this.sameSite = sameSite;
        this.cookieSecure = cookieSecure;
    }

    /* ========== LOGIN ========== */

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "redirect", required = false) String redirect, Model model) {
        model.addAttribute("redirect", redirect);
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        @RequestParam(required = false) String redirect,
                        HttpServletResponse response) {

        if (!userService.validateCredentials(username, password)) {
            return "redirect:/auth/login?error";
        }

        AppUser user = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Utilisateur introuvable après validation"));

        String token = jwtIssuer.issue(user.getUsername(), user.getRole().name());
        addJwtCookie(response, token);

        String target = (redirect != null && redirect.startsWith("/")) ? redirect : "/ui/patients";
        return "redirect:" + target;
    }

    /* ========== REGISTER ========== */

    @GetMapping("/register")
    public String registerPage(@RequestParam(value = "redirect", required = false) String redirect, Model model) {
        model.addAttribute("user", new AppUser());
        model.addAttribute("roles", UserRole.values());
        model.addAttribute("redirect", redirect);
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String password,
                           @RequestParam UserRole role,
                           @RequestParam(required = false) String redirect,
                           HttpServletResponse response) {

        Optional<AppUser> existing = userService.findByUsername(username);
        if (existing.isPresent()) {
            return "redirect:/auth/register?error=exists";
        }

        AppUser newUser = new AppUser();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setRole(role);
        userService.register(newUser);

        String token = jwtIssuer.issue(newUser.getUsername(), newUser.getRole().name());
        addJwtCookie(response, token);

        String target = (redirect != null && redirect.startsWith("/")) ? redirect : "/ui/patients";
        return "redirect:" + target;
    }

    /* ========== LOGOUT ========== */

    @PostMapping("/logout")
    public String logout(HttpServletResponse response) {
        expireJwtCookie(response);
        return "redirect:/auth/login?logout";
    }

    /* ========== UTILITAIRES COOKIES ========== */

    private void addJwtCookie(HttpServletResponse response, String token) {
        ResponseCookie rc = ResponseCookie.from(cookieName, token)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite(sameSite)
                .path("/")
                .maxAge(Duration.ofSeconds(ttlSeconds))
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, rc.toString());
    }

    private void expireJwtCookie(HttpServletResponse response) {
        ResponseCookie rc = ResponseCookie.from(cookieName, "")
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite(sameSite)
                .path("/")
                .maxAge(Duration.ZERO)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, rc.toString());
    }
}
