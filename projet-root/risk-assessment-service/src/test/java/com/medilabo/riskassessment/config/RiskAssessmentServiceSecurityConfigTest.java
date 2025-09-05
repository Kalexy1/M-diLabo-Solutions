package com.medilabo.riskassessment.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ActiveProfiles;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RiskAssessmentServiceSecurityConfigTest {

    @org.springframework.boot.test.context.TestConfiguration
    static class SecTestUsers {
        @org.springframework.context.annotation.Bean
        org.springframework.security.crypto.password.PasswordEncoder passwordEncoder() {
            return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
        }
        @org.springframework.context.annotation.Bean
        org.springframework.security.provisioning.InMemoryUserDetailsManager uds(
                org.springframework.security.crypto.password.PasswordEncoder enc) {
            var praticien = org.springframework.security.core.userdetails.User
                    .withUsername("praticien")
                    .password(enc.encode("motdepasse"))
                    .roles("PRATICIEN")
                    .build();
            return new org.springframework.security.provisioning.InMemoryUserDetailsManager(praticien);
        }
    }

    @org.springframework.beans.factory.annotation.Autowired
    private MockMvc mockMvc;

    // ⬇️ AJOUTE CECI
    @org.springframework.boot.test.mock.mockito.MockBean
    private com.medilabo.riskassessment.service.RiskAssessmentService riskAssessmentService;

    @org.junit.jupiter.api.Test
    @org.junit.jupiter.api.DisplayName("Accès refusé sans authentification")
    void shouldDenyAccessWhenNoAuth() throws Exception {
        mockMvc.perform(get("/assess/1"))
               .andExpect(status().isUnauthorized());
    }

    @org.junit.jupiter.api.Test
    @org.junit.jupiter.api.DisplayName("Accès autorisé avec authentification HTTP Basic")
    void shouldAllowAccessWithBasicAuth() throws Exception {
        // ⬇️ on évite l'appel réseau en mockant la réponse du service
        org.mockito.Mockito.when(riskAssessmentService.assessRiskDetailed(1L))
            .thenReturn(new com.medilabo.riskassessment.dto.RiskAssessmentResponse(
                1L, "John", "Doe", 45, "Borderline"
            ));

        String base64 = java.util.Base64.getEncoder()
                .encodeToString(("praticien:motdepasse").getBytes());

        mockMvc.perform(get("/assess/1")
                .header(HttpHeaders.AUTHORIZATION, "Basic " + base64)
                .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.firstName").value("John"))
               .andExpect(jsonPath("$.riskLevel").value("Borderline"));
    }
}
