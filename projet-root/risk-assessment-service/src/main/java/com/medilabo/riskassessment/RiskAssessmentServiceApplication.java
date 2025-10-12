package com.medilabo.riskassessment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * Point d’entrée du microservice <strong>risk-assessment-service</strong>.
 * <p>
 * Ce microservice est responsable de l’évaluation du risque de diabète
 * pour un patient donné. Il s’appuie sur :
 * <ul>
 *   <li>les informations personnelles du patient fournies par le microservice
 *       <strong>patient-service</strong>,</li>
 *   <li>et l’historique médical (notes) fourni par le microservice
 *       <strong>note-service</strong>.</li>
 * </ul>
 * <p>
 * Les appels inter-services sont effectués via HTTP à l’aide d’un
 * {@link RestTemplate}.
 * </p>
 */
@SpringBootApplication
public class RiskAssessmentServiceApplication {

    /**
     * Démarre l’application Spring Boot.
     *
     * @param args les arguments de la ligne de commande
     */
    public static void main(String[] args) {
        SpringApplication.run(RiskAssessmentServiceApplication.class, args);
    }

    /**
     * Fournit un bean {@link RestTemplate} pour effectuer les appels HTTP
     * vers les autres microservices via la Gateway.
     *
     * @return une instance de {@link RestTemplate}
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
