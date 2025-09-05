package com.medilabo.auth.controller;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.medilabo.auth.config.TestSecurityConfig;
import com.medilabo.auth.model.AppUser;
import com.medilabo.auth.service.UserService;

@WebMvcTest(AuthController.class)
@Import(TestSecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void shouldDisplayLoginForm() throws Exception {
        mockMvc.perform(get("/auth/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    void shouldDisplayRegisterForm() throws Exception {
        mockMvc.perform(get("/auth/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }

    @Test
    void shouldRegisterUserAndRedirect() throws Exception {
        when(userService.userExists("john")).thenReturn(false);
        when(userService.saveUser(Mockito.any(AppUser.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/auth/register")
                        .param("username", "john")
                        .param("password", "pass")
                        .param("role", "ORGANISATEUR")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));
    }

    @Test
    void shouldRejectIfUsernameExists() throws Exception {
        when(userService.userExists("john")).thenReturn(true);

        mockMvc.perform(post("/auth/register")
                        .param("username", "john")
                        .param("password", "pass")
                        .param("role", "PRATICIEN")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    void testRegisterWithExistingUsername_shouldShowError() throws Exception {
        when(userService.userExists("john")).thenReturn(true);

        mockMvc.perform(post("/auth/register")
                        .param("username", "john")
                        .param("password", "newpwd")
                        .param("role", "ORGANISATEUR")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    void testAccessDeniedWithoutLogin_shouldRedirectToLogin() throws Exception {
        mockMvc.perform(get("/patients"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/auth/login"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ORGANISATEUR")
    void testLogout_shouldRedirectToLoginPage() throws Exception {
        mockMvc.perform(post("/auth/logout").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login?logout"));
    }
}
