package com.medilabo.riskassessment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Point d’entrée principal du microservice <strong>risk-assessment-service</strong>.
 *
 * <p>
 * Ce service calcule le niveau de risque de diabète pour un patient donné en
 * interrogeant deux microservices :
 * </p>
 * <ul>
 *     <li><strong>patient-service</strong> : informations personnelles du patient</li>
 *     <li><strong>note-service</strong> : historique médical et notes d’observation</li>
 * </ul>
 *
 * <p>
 * Les appels inter-services sont réalisés à l’aide d’un {@link RestTemplate},
 * configuré pour propager automatiquement l’en-tête <code>Authorization</code>
 * présent dans la requête entrante.
 * </p>
 */
@SpringBootApplication
public class RiskAssessmentServiceApplication {

    /**
     * Démarre l’application Spring Boot.
     *
     * @param args arguments de la ligne de commande
     */
    public static void main(String[] args) {
        SpringApplication.run(RiskAssessmentServiceApplication.class, args);
    }

    /**
     * Fournit un {@link RestTemplate} configuré pour propager automatiquement
     * le header <code>Authorization</code> vers les microservices appelés.
     *
     * <p>
     * Cette propagation garantit que les appels effectués par ce service
     * conservent le contexte d’authentification de la requête d’origine,
     * permettant ainsi au Gateway et aux autres services de valider le JWT.
     * </p>
     *
     * @return un RestTemplate configuré avec un interceptor d’authentification
     */
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        restTemplate.getInterceptors().add((request, body, execution) -> {
            var attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                String authHeader = attrs.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
                if (StringUtils.hasText(authHeader)) {
                    request.getHeaders().set(HttpHeaders.AUTHORIZATION, authHeader);
                }
            }
            return execution.execute(request, body);
        });

        return restTemplate;
    }
}
