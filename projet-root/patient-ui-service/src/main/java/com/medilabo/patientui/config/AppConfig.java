package com.medilabo.patientui.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Fournit des WebClient préconfigurés vers la Gateway.
 * L’ajout du header Authorization se fait au niveau des services (en récupérant le JWT du cookie).
 */
@Configuration
public class AppConfig {

    @Bean
    public WebClient patientApiClient(@Value("${PATIENT_API_BASE_URL}") String baseUrl) {
        return WebClient.builder().baseUrl(baseUrl).build();
    }

    @Bean
    public WebClient noteApiClient(@Value("${NOTE_API_BASE_URL}") String baseUrl) {
        return WebClient.builder().baseUrl(baseUrl).build();
    }

    @Bean
    public WebClient riskApiClient(@Value("${RISK_API_BASE_URL}") String baseUrl) {
        return WebClient.builder().baseUrl(baseUrl).build();
    }
}
