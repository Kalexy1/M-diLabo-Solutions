package com.medilabo.patientui.service;

import com.medilabo.patientui.model.RiskAssessmentResponse;
import com.medilabo.patientui.web.JwtCookieUtil;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

class RiskServiceTest {

    private RestTemplate restTemplate;
    private MockRestServiceServer server;
    private RiskService riskService;

    @BeforeEach
    void setUp() {
        // RestTemplate configuré comme dans AppConfig : rootUri = base URL de l’API risk
        restTemplate = new RestTemplate();
        restTemplate.setUriTemplateHandler(
                new DefaultUriBuilderFactory("http://example.test/api/risk")
        );

        server = MockRestServiceServer.bindTo(restTemplate).build();

        // En prod Spring injecte le bean "riskApiClient", ici on passe ce RestTemplate de test
        riskService = new RiskService(restTemplate);
    }

    @Test
    void getRisk_withJwt_addsBearerHeader_andReturnsBody() {
        String json = """
            {
              "riskLevel": "IN_DANGER"
            }
            """;

        // RiskService appelle path "/7" → URL finale = http://example.test/api/risk/7
        server.expect(once(),
                      requestTo("http://example.test/api/risk/7"))
              .andExpect(method(GET))
              .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer jwt-123"))
              .andExpect(header(HttpHeaders.COOKIE, JwtCookieUtil.DEFAULT_COOKIE_NAME + "=jwt-123"))
              .andRespond(withStatus(HttpStatus.OK)
                      .contentType(MediaType.APPLICATION_JSON)
                      .body(json));

        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        servletRequest.setCookies(new Cookie("JWT_TOKEN", "jwt-123"));

        RiskAssessmentResponse out = riskService.getRisk(7L, servletRequest);

        server.verify();
        assertThat(out).isNotNull();
        assertThat(out.getRiskLevel()).isEqualTo("IN_DANGER");
    }

    @Test
    void getRisk_withoutJwt_doesNotSendAuthorization_andReturnsBody() {
        String json = """
            {
              "riskLevel": "LOW"
            }
            """;

        // Aucun JWT → pas d'Authorization ni de Cookie JWT_TOKEN
        server.expect(once(),
                      requestTo("http://example.test/api/risk/42"))
              .andExpect(method(GET))
              .andExpect(headerDoesNotExist(HttpHeaders.AUTHORIZATION))
              .andExpect(headerDoesNotExist(HttpHeaders.COOKIE))
              .andRespond(withStatus(HttpStatus.OK)
                      .contentType(MediaType.APPLICATION_JSON)
                      .body(json));

        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        // pas de cookie JWT_TOKEN

        RiskAssessmentResponse out = riskService.getRisk(42L, servletRequest);

        server.verify();
        assertThat(out).isNotNull();
        assertThat(out.getRiskLevel()).isEqualTo("LOW");
    }
}
