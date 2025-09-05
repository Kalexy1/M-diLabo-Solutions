package com.medilabo.riskassessment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * Application: com.medilabo.riskassessment
 * <p>
 * Classe <strong>RiskAssessmentServiceApplication</strong>.
 * <br/>
 * Rôle : Point d’entrée principal du microservice <em>risk-assessment-service</em>.
 * </p>
 * <p>
 * Ce microservice est chargé d’évaluer le risque de diabète d’un patient
 * en s’appuyant sur :
 * <ul>
 *   <li>Les informations personnelles récupérées auprès du <em>patient-service</em>.</li>
 *   <li>L’historique médical (notes) récupéré auprès du <em>note-service</em>.</li>
 * </ul>
 * Les appels sont effectués via HTTP grâce à un {@link RestTemplate}.
 * </p>
 */
@SpringBootApplication
public class RiskAssessmentServiceApplication {

    /**
     * Méthode principale qui démarre l’application Spring Boot.
     *
     * @param args arguments passés en ligne de commande
     */
    public static void main(String[] args) {
        SpringApplication.run(RiskAssessmentServiceApplication.class, args);
    }

    /**
     * Déclare un {@link RestTemplate} comme bean Spring.
     * <p>
     * Ce client HTTP est utilisé pour interroger les autres microservices
     * (par ex. {@code patient-service} et {@code note-service}) via la Gateway
     * afin de collecter les données nécessaires au calcul du risque.
     * </p>
     *
     * @return une instance configurée de {@link RestTemplate}
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
