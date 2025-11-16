package com.medilabo.riskassessment.controller;

import com.medilabo.riskassessment.dto.RiskAssessmentResponse;
import com.medilabo.riskassessment.service.RiskAssessmentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RiskAssessmentController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class RiskAssessmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RiskAssessmentService riskAssessmentService;

    @Test
    @DisplayName("GET /api/risk/{id} -> 200 OK et corps attendu (sécurité gérée par le gateway)")
    void shouldReturnRiskWithoutLocalSecurity() throws Exception {
        when(riskAssessmentService.assessRiskDetailed(1L))
                .thenReturn(new RiskAssessmentResponse(1L, "John", "Doe", 45, "Borderline"));

        mockMvc.perform(get("/api/risk/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.patientId").value(1))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.age").value(45))
                .andExpect(jsonPath("$.riskLevel").value("Borderline"));
    }
}
