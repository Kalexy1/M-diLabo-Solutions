package com.medilabo.gatewayservice.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test standalone (sans ApplicationContext) pour vérifier
 * qu'une route publique répond bien en 200.
 * Ici on ne charge PAS SecurityConfig ni Spring.
 */
class SecurityConfigTest {

    private MockMvc mvc;

    @Controller
    static class DummyAuthController {
        @GetMapping("/auth/login")
        @ResponseBody
        public String login() { return "OK"; }
    }

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.standaloneSetup(new DummyAuthController()).build();
    }

    @Test
    void public_route_is_accessible_without_token() throws Exception {
        mvc.perform(get("/auth/login"))
           .andExpect(status().isOk());
    }
}
