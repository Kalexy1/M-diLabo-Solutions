package com.medilabo.riskassessment.controller;

import com.medilabo.riskassessment.dto.RiskAssessmentResponse;
import com.medilabo.riskassessment.service.RiskAssessmentService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RiskAssessmentController.class)
class RiskAssessmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RiskAssessmentService riskService;

    @Test
    void shouldReturnRiskAssessmentResponse() throws Exception {
        Long patientId = 1L;
        RiskAssessmentResponse response = new RiskAssessmentResponse();
        response.setPatientId(patientId);
        response.setAge(40);
        response.setRiskLevel("None");

        when(riskService.assessRiskDetailed(patientId)).thenReturn(response);

        mockMvc.perform(get("/assess/{patientId}", patientId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.patientId").value(patientId))
                .andExpect(jsonPath("$.age").value(40))
                .andExpect(jsonPath("$.riskLevel").value("None"));
    }
    
    @Test
    void shouldReturnRiskLevelEarlyOnset() throws Exception {
        Long patientId = 2L;
        RiskAssessmentResponse response = new RiskAssessmentResponse();
        response.setPatientId(patientId);
        response.setAge(55);
        response.setRiskLevel("Early onset");

        when(riskService.assessRiskDetailed(patientId)).thenReturn(response);

        mockMvc.perform(get("/assess/{patientId}", patientId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.riskLevel").value("Early onset"));
    }
}
