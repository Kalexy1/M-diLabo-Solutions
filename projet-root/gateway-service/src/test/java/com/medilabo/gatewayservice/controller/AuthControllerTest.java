package com.medilabo.gatewayservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.medilabo.gatewayservice.TestSecurityConfig;
import com.medilabo.gatewayservice.jwt.JwtIssuer;
import com.medilabo.gatewayservice.model.AppUser;
import com.medilabo.gatewayservice.model.UserRole;
import com.medilabo.gatewayservice.service.UserService;

@WebMvcTest(AuthController.class)
@Import(TestSecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtIssuer jwtIssuer;

    @MockBean
    private PasswordEncoder passwordEncoder;

    // ------------------------------
    // LOGIN PAGE
    // ------------------------------

    @Test
    void testLoginPage() throws Exception {
        mvc.perform(get("/auth/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    // ------------------------------
    // LOGIN - FAIL
    // ------------------------------

    @Test
    void testLoginInvalidCredentials() throws Exception {
        when(userService.validateCredentials("john", "pwd"))
                .thenReturn(false);

        mvc.perform(post("/auth/login")
                        .param("username", "john")
                        .param("password", "pwd"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login?error"));
    }

    // ------------------------------
    // LOGIN - SUCCESS
    // ------------------------------

    @Test
    void testLoginSuccess() throws Exception {

        AppUser user = new AppUser();
        user.setUsername("john");
        user.setRole(UserRole.ORGANISATEUR);

        when(userService.validateCredentials("john", "pwd"))
                .thenReturn(true);
        when(userService.findByUsername("john"))
                .thenReturn(Optional.of(user));

        when(jwtIssuer.issue("john", "ORGANISATEUR"))
                .thenReturn("TOKEN");

        mvc.perform(post("/auth/login")
                        .param("username", "john")
                        .param("password", "pwd"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ui/patients"))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, org.hamcrest.Matchers.containsString("TOKEN")));
    }

    // ------------------------------
    // REGISTER PAGE
    // ------------------------------

    @Test
    void testRegisterPage() throws Exception {
        mvc.perform(get("/auth/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }

    // ------------------------------
    // REGISTER - USER EXISTS
    // ------------------------------

    @Test
    void testRegisterUserAlreadyExists() throws Exception {
        when(userService.findByUsername("john"))
                .thenReturn(Optional.of(new AppUser()));

        mvc.perform(post("/auth/register")
                        .param("username", "john")
                        .param("password", "pwd")
                        .param("role", "ORGANISATEUR"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/register?error=exists"));
    }

    // ------------------------------
    // REGISTER - SUCCESS
    // ------------------------------

    @Test
    void testRegisterSuccess() throws Exception {

        when(userService.findByUsername("john"))
                .thenReturn(Optional.empty());

        when(jwtIssuer.issue("john", "ORGANISATEUR"))
                .thenReturn("TOKEN");

        // userService.register ne renvoie rien d’important : on simule juste l'appel
        when(userService.register(any(AppUser.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        mvc.perform(post("/auth/register")
                        .param("username", "john")
                        .param("password", "pwd")
                        .param("role", "ORGANISATEUR"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ui/patients"))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, org.hamcrest.Matchers.containsString("TOKEN")));
    }

    // ------------------------------
    // LOGOUT
    // ------------------------------

    @Test
    void testLogout() throws Exception {
        mvc.perform(post("/auth/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login?logout"))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, org.hamcrest.Matchers.containsString("Max-Age=0")));
    }
}
