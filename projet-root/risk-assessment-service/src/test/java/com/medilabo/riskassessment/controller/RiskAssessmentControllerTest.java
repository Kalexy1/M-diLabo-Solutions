package com.medilabo.riskassessment.controller;

import com.medilabo.riskassessment.config.RiskAssessmentServiceSecurityConfig;
import com.medilabo.riskassessment.dto.RiskAssessmentResponse;
import com.medilabo.riskassessment.service.RiskAssessmentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RiskAssessmentController.class)
@AutoConfigureMockMvc(addFilters = true)
@Import(RiskAssessmentServiceSecurityConfig.class)
class RiskAssessmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RiskAssessmentService riskService;

    @Test
    @DisplayName("Doit retourner 200 avec rôle PRATICIEN")
    @WithMockUser(username = "praticien", roles = "PRATICIEN")
    void shouldReturnRiskWithAuthorizedUser() throws Exception {
        RiskAssessmentResponse response =
                new RiskAssessmentResponse(1L, "John", "Doe", 50, "In Danger");

        Mockito.when(riskService.assessRiskDetailed(eq(1L))).thenReturn(response);

        mockMvc.perform(get("/assess/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.riskLevel").value("In Danger"));
    }

    @Test
    @DisplayName("Doit refuser l’accès sans authentification")
    void shouldRejectRequestWithoutAuth() throws Exception {
        mockMvc.perform(get("/assess/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Doit refuser l’accès avec mauvais rôle")
    @WithMockUser(username = "user", roles = "ORGANISATEUR")
    void shouldRejectAccessForWrongRole() throws Exception {
        mockMvc.perform(get("/assess/1"))
                .andExpect(status().isForbidden());
    }
}
