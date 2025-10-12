package com.medilabo.patientui.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuration des clients Web pour la communication entre le microservice
 * <strong>patient-ui-service</strong> et les autres microservices de l’application Medilabo.
 * <p>
 * Cette classe définit des {@link WebClient} préconfigurés pour interagir
 * avec la Gateway ou directement avec les services patients, notes et risques.
 * <br/>
 * L’ajout du header {@code Authorization} (JWT) est géré au niveau des services appelants.
 * </p>
 */
@Configuration
public class AppConfig {

    /**
     * Crée un {@link WebClient} configuré pour communiquer avec le microservice Patient.
     *
     * @param baseUrl l’URL de base du service Patient (fournie via la variable d’environnement {@code PATIENT_API_BASE_URL})
     * @return une instance de {@link WebClient} configurée pour le service Patient
     */
    @Bean
    public WebClient patientApiClient(@Value("${PATIENT_API_BASE_URL}") String baseUrl) {
        return WebClient.builder().baseUrl(baseUrl).build();
    }

    /**
     * Crée un {@link WebClient} configuré pour communiquer avec le microservice Note.
     *
     * @param baseUrl l’URL de base du service Note (fournie via la variable d’environnement {@code NOTE_API_BASE_URL})
     * @return une instance de {@link WebClient} configurée pour le service Note
     */
    @Bean
    public WebClient noteApiClient(@Value("${NOTE_API_BASE_URL}") String baseUrl) {
        return WebClient.builder().baseUrl(baseUrl).build();
    }

    /**
     * Crée un {@link WebClient} configuré pour communiquer avec le microservice Risk Assessment.
     *
     * @param baseUrl l’URL de base du service Risk Assessment (fournie via la variable d’environnement {@code RISK_API_BASE_URL})
     * @return une instance de {@link WebClient} configurée pour le service Risk Assessment
     */
    @Bean
    public WebClient riskApiClient(@Value("${RISK_API_BASE_URL}") String baseUrl) {
        return WebClient.builder().baseUrl(baseUrl).build();
    }
}
