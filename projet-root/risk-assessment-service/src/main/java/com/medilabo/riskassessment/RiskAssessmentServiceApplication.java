package com.medilabo.riskassessment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * Classe principale du microservice Risk Assessment.
 * <p>
 * Ce microservice analyse les données d’un patient pour déterminer son niveau de risque de diabète.
 * Il interroge les microservices patient-service et note-service via HTTP.
 * </p>
 */
@SpringBootApplication
public class RiskAssessmentServiceApplication {

    /**
     * Point d'entrée principal du microservice.
     *
     * @param args les arguments passés en ligne de commande
     */
    public static void main(String[] args) {
        SpringApplication.run(RiskAssessmentServiceApplication.class, args);
    }

    /**
     * Déclare un {@link RestTemplate} en tant que bean Spring pour permettre les appels REST
     * vers les autres microservices (ex. : patient-service, note-service).
     *
     * @return une instance de {@link RestTemplate}
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
