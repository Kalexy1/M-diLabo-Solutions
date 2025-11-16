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
 * Service UI consommant l’API RiskAssessment via RestTemplate.
 *
 * Le RestTemplate injecté est celui nommé "riskApiClient", défini dans AppConfig :
 *
 *     @Bean("riskApiClient")
 *     public RestTemplate riskApiClient(@Value("${risk.api.url}") String url) {
 *         return restTemplateBase(url);
 *     }
 *
 * L’URL de base est donc déjà configurée et les appels ici utilisent
 * uniquement des chemins relatifs : "/{patientId}".
 */
@Service
public class RiskService {

    private final RestTemplate apiClient;

    public RiskService(@Qualifier("riskApiClient") RestTemplate apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Construit les en-têtes HTTP avec JWT (Authorization + Cookie).
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
     * Appel générique vers l'API risk-assessment.
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

    // ============================================================
    //                    MÉTHODES PUBLIQUES
    // ============================================================

    /**
     * Récupère le niveau de risque de diabète pour un patient.
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
