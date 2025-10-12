package com.medilabo.auth.controller;

import com.medilabo.auth.model.AppUser;
import com.medilabo.auth.model.UserRole;
import com.medilabo.auth.security.JwtIssuer;
import com.medilabo.auth.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
    "security.jwt.cookie.name=JWT_TOKEN",
    "security.jwt.ttl-seconds=43200",
    "security.jwt.cookie.secure=false",
    "security.jwt.cookie.samesite=Lax",
    // on simule le gateway en local pour les redirections absolues du contrôleur
    "ui.base-url=http://localhost:8080"
})
class AuthControllerTest {

    @Autowired MockMvc mockMvc;

    @MockBean UserService userService;
    @MockBean JwtIssuer jwtIssuer;

    AppUser user;

    @BeforeEach
    void setup() {
        user = new AppUser();
        user.setId(1L);
        user.setUsername("med");
        user.setPassword("{bcrypt}xxx");
        user.setRole(UserRole.PRATICIEN);
    }

    @Test
    void login_success_setsJwtCookie_and_redirectsToUi() throws Exception {
        when(userService.validateCredentials("med", "pwd")).thenReturn(true);
        when(userService.findByUsername("med")).thenReturn(Optional.of(user));
        when(jwtIssuer.issue(ArgumentMatchers.eq("med"), ArgumentMatchers.anyList()))
            .thenReturn("jwt-token");

        mockMvc.perform(post("/auth/login")
                .param("username", "med")
                .param("password", "pwd")
                .with(csrf()))
            .andExpect(status().is3xxRedirection())
            // accepte soit l'ancienne redirection relative, soit la nouvelle absolue vers le gateway
            .andExpect(header().string("Location", anyOf(
                endsWith("/ui/"),
                endsWith("/ui/patients")
            )))
            .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("JWT_TOKEN=jwt-token")))
            .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("HttpOnly")))
            .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("Path=/")));
    }

    @Test
    void login_failure_redirects_with_error() throws Exception {
        when(userService.validateCredentials("med", "bad")).thenReturn(false);

        mockMvc.perform(post("/auth/login")
                .param("username", "med")
                .param("password", "bad")
                .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(header().string("Location",
                anyOf(
                    endsWith("/auth/login?error"),
                    endsWith("/auth/login?error=true")
                )));
    }

    @Test
    void register_success_autoLogin_setsJwtCookie_and_redirectsToUi() throws Exception {
        when(userService.register(ArgumentMatchers.any(AppUser.class)))
            .thenAnswer(inv -> {
                AppUser u = inv.getArgument(0);
                u.setId(2L);
                return u;
            });
        when(jwtIssuer.issue(ArgumentMatchers.eq("newuser"), ArgumentMatchers.anyList()))
            .thenReturn("jwt-token");

        mockMvc.perform(post("/auth/register")
                .param("username", "newuser")
                .param("password", "pwd123")
                .param("role", "PRATICIEN")
                .with(csrf()))
            .andExpect(status().is3xxRedirection())
            // accepte relative /ui/ ou absolue vers /ui/patients
            .andExpect(header().string("Location", anyOf(
                endsWith("/ui/"),
                endsWith("/ui/patients")
            )))
            .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("JWT_TOKEN=jwt-token")));
    }
}
