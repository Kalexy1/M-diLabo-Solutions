package com.medilabo.gatewayservice.controller;

import com.medilabo.gatewayservice.jwt.JwtIssuer;
import com.medilabo.gatewayservice.model.AppUser;
import com.medilabo.gatewayservice.model.UserRole;
import com.medilabo.gatewayservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.matchesPattern;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test unitaire du AuthController sans démarrer Spring.
 * - MockMvc en mode standalone (pas d'ApplicationContext)
 * - UserService, JwtIssuer & PasswordEncoder mockés
 * - Compatible avec le constructeur à 7 paramètres du contrôleur
 */
class AuthControllerTest {

    private MockMvc mvc;

    private UserService userService;
    private JwtIssuer jwtIssuer;
    private PasswordEncoder passwordEncoder;

    private AppUser user;

    @BeforeEach
    void setup() {
        userService = Mockito.mock(UserService.class);
        jwtIssuer = Mockito.mock(JwtIssuer.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);

        // ✅ Constructeur à 7 paramètres (nouvelle version)
        AuthController controller = new AuthController(
                userService,
                jwtIssuer,
                passwordEncoder,
                "JWT_TOKEN",   // cookieName
                43200,         // ttlSeconds
                "Lax",         // sameSite
                false          // cookieSecure
        );

        // MockMvc standalone (aucun filtre de sécurité)
        mvc = MockMvcBuilders.standaloneSetup(controller).build();

        user = new AppUser();
        user.setId(1L);
        user.setUsername("med");
        user.setPassword("{bcrypt}hash");
        user.setRole(UserRole.PRATICIEN);
    }

    @Test
    void login_success_redirects_and_sets_cookie() throws Exception {
        when(userService.validateCredentials("med", "pwd")).thenReturn(true);
        when(userService.findByUsername("med")).thenReturn(Optional.of(user));
        when(jwtIssuer.issue(eq("med"), anyString())).thenReturn("jwt-token");

        var result = mvc.perform(post("/auth/login")
                        .param("username", "med")
                        .param("password", "pwd"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", matchesPattern(".*/ui(/patients)?$")))
                .andExpect(header().exists(HttpHeaders.SET_COOKIE))
                .andReturn();

        String setCookie = result.getResponse().getHeader(HttpHeaders.SET_COOKIE);
        assertThat(setCookie).isNotNull();
        assertThat(setCookie).contains("JWT_TOKEN=jwt-token");

        verify(userService).validateCredentials("med", "pwd");
        verify(userService).findByUsername("med");
        verify(jwtIssuer).issue(eq("med"), anyString());
    }

    @Test
    void login_failure_redirects_to_error() throws Exception {
        when(userService.validateCredentials("med", "bad")).thenReturn(false);

        mvc.perform(post("/auth/login")
                        .param("username", "med")
                        .param("password", "bad"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", containsString("/auth/login?error")));

        verify(userService).validateCredentials("med", "bad");
        verify(userService, never()).findByUsername(anyString());
        verify(jwtIssuer, never()).issue(anyString(), anyString());
    }

    @Test
    void logout_clears_cookie_and_redirects() throws Exception {
        var result = mvc.perform(post("/auth/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", containsString("/auth/login?logout")))
                .andExpect(header().exists(HttpHeaders.SET_COOKIE))
                .andReturn();

        String setCookie = result.getResponse().getHeader(HttpHeaders.SET_COOKIE);
        assertThat(setCookie).isNotNull();
        assertThat(setCookie).contains("JWT_TOKEN=");
    }
}
