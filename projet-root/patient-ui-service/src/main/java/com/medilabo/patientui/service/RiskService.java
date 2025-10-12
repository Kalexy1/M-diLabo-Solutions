package com.medilabo.patientui.service;

import com.medilabo.patientui.model.RiskAssessmentResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class RiskService {
    private final WebClient riskApiClient;

    public RiskService(WebClient riskApiClient) {
        this.riskApiClient = riskApiClient;
    }

    public RiskAssessmentResponse getRisk(Long patientId, String jwt) {
        return riskApiClient.get()
                .uri(b -> b.path("/{pid}").build(patientId))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + (jwt == null ? "" : jwt))
                .retrieve()
                .bodyToMono(RiskAssessmentResponse.class)
                .block();
    }
}
