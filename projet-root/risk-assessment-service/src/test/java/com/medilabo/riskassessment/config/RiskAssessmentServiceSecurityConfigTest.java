package com.medilabo.riskassessment.config;

import com.medilabo.riskassessment.dto.RiskAssessmentResponse;
import com.medilabo.riskassessment.service.RiskAssessmentService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RiskAssessmentServiceSecurityConfigTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private RiskAssessmentService riskAssessmentService;

  @Test
  void shouldDenyAccessWhenNoAuth() throws Exception {
    mockMvc.perform(get("/risk/1"))
           .andExpect(status().isUnauthorized());
  }

  @Test
  void shouldAllowAccessWithJwtRolePraticien() throws Exception {
    when(riskAssessmentService.assessRiskDetailed(1L))
        .thenReturn(new RiskAssessmentResponse(1L, "John", "Doe", 45, "Borderline"));

    mockMvc.perform(get("/risk/1")
            .with(jwt().authorities(List.of(new SimpleGrantedAuthority("ROLE_PRATICIEN")))))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.firstName").value("John"))
           .andExpect(jsonPath("$.riskLevel").value("Borderline"));
  }

  @Test
  void shouldForbidWithWrongRole() throws Exception {
    mockMvc.perform(get("/risk/1")
            .with(jwt().authorities(List.of(new SimpleGrantedAuthority("ROLE_ORGANISATEUR")))))
           .andExpect(status().isForbidden());
  }
}
