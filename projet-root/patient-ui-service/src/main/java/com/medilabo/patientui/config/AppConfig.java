package com.medilabo.patientui.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Classe de configuration de l'application Patient UI Service.
 * <p>
 * Déclare les beans nécessaires, notamment le {@link RestTemplate} pour effectuer
 * des appels HTTP vers d'autres microservices.
 * </p>
 */
@Configuration
public class AppConfig {

    /**
     * Déclare un bean {@link RestTemplate} pour permettre la communication avec les microservices
     * comme patient-service, note-service ou risk-assessment-service.
     *
     * @return une instance de {@link RestTemplate}
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
