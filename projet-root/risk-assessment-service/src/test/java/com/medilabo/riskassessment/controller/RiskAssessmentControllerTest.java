package com.medilabo.riskassessment.controller;

import com.medilabo.riskassessment.config.RiskAssessmentServiceSecurityConfig;
import com.medilabo.riskassessment.dto.RiskAssessmentResponse;
import com.medilabo.riskassessment.service.RiskAssessmentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RiskAssessmentController.class)
@Import(RiskAssessmentServiceSecurityConfig.class)
@ActiveProfiles("test")
class RiskAssessmentControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private RiskAssessmentService riskAssessmentService;

  @Test
  @DisplayName("200 OK pour ROLE_PRATICIEN")
  void shouldReturnRiskWithAuthorizedUser() throws Exception {
    when(riskAssessmentService.assessRiskDetailed(1L))
        .thenReturn(new RiskAssessmentResponse(1L, "John", "Doe", 45, "Borderline"));

    mockMvc.perform(get("/risk/1")
        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_PRATICIEN"))))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.firstName").value("John"))
      .andExpect(jsonPath("$.riskLevel").value("Borderline"));
  }

  @Test
  @DisplayName("403 Forbidden pour mauvais rôle")
  void shouldRejectAccessForWrongRole() throws Exception {
    mockMvc.perform(get("/risk/1")
        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ORGANISATEUR"))))
      .andExpect(status().isForbidden());
  }
}
