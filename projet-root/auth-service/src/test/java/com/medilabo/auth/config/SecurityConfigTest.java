package com.medilabo.auth.config;

import com.medilabo.auth.controller.HomeController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = HomeController.class)
@Import(SecurityConfig.class)
class SecurityConfigTest {

    @Autowired MockMvc mockMvc;
    @Autowired SecurityFilterChain chain;

    @Test
    void securityFilterChain_loaded() {
        assertThat(chain).isNotNull();
    }

    @Test
    void root_redirects_to_auth_login() throws Exception {
        mockMvc.perform(get("/"))
               .andExpect(status().is3xxRedirection())
               .andExpect(header().string("Location", endsWith("/login")));
    }

    @Test
    void static_paths_are_404_when_no_resource() throws Exception {
        mockMvc.perform(get("/css/x.css")).andExpect(status().isNotFound());
        mockMvc.perform(get("/js/x.js")).andExpect(status().isNotFound());
        mockMvc.perform(get("/images/x.png")).andExpect(status().isNotFound());
    }

    @Test
    void private_path_redirects_to_login() throws Exception {
        mockMvc.perform(get("/private"))
               .andExpect(status().is3xxRedirection())
               .andExpect(header().string("Location", endsWith("/login")));
    }
}
