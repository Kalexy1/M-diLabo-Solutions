package com.medilabo.patientui.service;

import com.medilabo.patientui.model.RiskAssessmentResponse;
import com.medilabo.patientui.web.JwtCookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * Service chargé de consommer l’API du microservice
 * <strong>risk-assessment-service</strong> via la Gateway.
 *
 * <p>
 * Le {@link RestTemplate} injecté (bean <code>riskApiClient</code>)
 * possède déjà l’URL de base configurée dans <code>application.yml</code>.
 * Les appels effectués ici utilisent donc uniquement des chemins relatifs
 * (ex. <code>"/{patientId}"</code>).
 * </p>
 *
 * <p>
 * Les requêtes incluent automatiquement le JWT présent dans le cookie
 * utilisateur afin de garantir l’authentification côté backend.
 * </p>
 */
@Service
public class RiskService {

    /** Client REST dédié aux appels vers l’API RiskAssessment. */
    private final RestTemplate apiClient;

    public RiskService(@Qualifier("riskApiClient") RestTemplate apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Construit les en-têtes HTTP nécessaires, incluant le token JWT
     * (Authorization Bearer + Cookie).
     *
     * @param request requête HTTP contenant le cookie JWT
     * @return en-têtes complets pour l’appel REST
     */
    private HttpHeaders buildAuthHeaders(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));

        String jwt = JwtCookieUtil.extractJwt(request);
        if (jwt != null && !jwt.isBlank()) {
            headers.setBearerAuth(jwt);
            headers.add(HttpHeaders.COOKIE, JwtCookieUtil.DEFAULT_COOKIE_NAME + "=" + jwt);
        }

        return headers;
    }

    /**
     * Appel REST générique vers le microservice risk-assessment.
     *
     * @param path chemin relatif (ex. <code>"/5"</code>)
     * @param method méthode HTTP à utiliser
     * @param body corps de la requête (ou {@code null})
     * @param request requête HTTP contenant le JWT
     * @param responseType type attendu en réponse
     * @param <T> type générique retourné
     * @return réponse désérialisée
     * @throws IllegalStateException en cas d’erreur HTTP renvoyée par le backend
     */
    private <T> T callApi(String path,
                          HttpMethod method,
                          Object body,
                          HttpServletRequest request,
                          Class<T> responseType) {

        HttpHeaders headers = buildAuthHeaders(request);

        HttpEntity<?> entity = (body != null)
                ? new HttpEntity<>(body, headers)
                : new HttpEntity<>(headers);

        try {
            ResponseEntity<T> response =
                    apiClient.exchange(path, method, entity, responseType);
            return response.getBody();

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new IllegalStateException(
                    "Risk API error " + e.getStatusCode() + " : " + e.getResponseBodyAsString(),
                    e
            );
        }
    }

    /**
     * Récupère le rapport d’évaluation du risque de diabète
     * pour un patient donné.
     *
     * @param patientId identifiant du patient
     * @param request requête HTTP contenant le JWT
     * @return objet DTO contenant le niveau de risque et les informations patient
     */
    public RiskAssessmentResponse getRisk(Long patientId, HttpServletRequest request) {
        return callApi(
                "/" + patientId,
                HttpMethod.GET,
                null,
                request,
                RiskAssessmentResponse.class
        );
    }
}
