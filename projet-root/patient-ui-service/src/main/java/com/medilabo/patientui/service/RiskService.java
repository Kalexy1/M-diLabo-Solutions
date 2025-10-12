package com.medilabo.patientui.service;

import com.medilabo.patientui.model.RiskAssessmentResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Service responsable de la communication avec le microservice
 * <strong>risk-assessment-service</strong>.
 * <p>
 * Ce service permet de récupérer le niveau de risque d’un patient
 * en fonction de ses informations médicales et de ses notes.
 * Le JWT est utilisé pour authentifier la requête auprès du service distant.
 * </p>
 */
@Service
public class RiskService {

    /**
     * Client Web configuré pour communiquer avec le microservice d’évaluation du risque.
     */
    private final WebClient riskApiClient;

    /**
     * Constructeur du service d’évaluation du risque.
     *
     * @param riskApiClient le client Web préconfiguré pour le microservice de risque
     */
    public RiskService(WebClient riskApiClient) {
        this.riskApiClient = riskApiClient;
    }

    /**
     * Récupère le niveau de risque de diabète pour un patient donné.
     *
     * @param patientId identifiant du patient à évaluer
     * @param jwt       jeton JWT pour l’authentification
     * @return un objet {@link RiskAssessmentResponse} contenant le niveau de risque
     * et le nombre de déclencheurs détectés
     */
    public RiskAssessmentResponse getRisk(Long patientId, String jwt) {
        return riskApiClient.get()
                .uri(b -> b.path("/{pid}").build(patientId))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + (jwt == null ? "" : jwt))
                .retrieve()
                .bodyToMono(RiskAssessmentResponse.class)
                .block();
    }
}
