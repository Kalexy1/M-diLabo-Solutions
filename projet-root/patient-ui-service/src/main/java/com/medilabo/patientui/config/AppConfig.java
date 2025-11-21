package com.medilabo.patientui.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

/**
 * Configuration applicative du microservice patient-ui-service.
 *
 * <p>Cette classe fournit des instances de {@link RestTemplate} préconfigurées
 * pour communiquer avec les microservices Patients, Notes et Risk Assessment.</p>
 *
 * <p>Chaque {@code RestTemplate} est associé à un {@code baseUrl} défini
 * dans les propriétés de configuration, facilitant ainsi la construction
 * des URI des endpoints distants.</p>
 */
@Configuration
public class AppConfig {

    /**
     * Construit un client HTTP {@link RestTemplate} avec un base URL prédéfini.
     *
     * @param baseUrl URL de base du microservice cible
     * @return un RestTemplate configuré
     */
    private RestTemplate buildClient(String baseUrl) {
        RestTemplate rt = new RestTemplate();
        rt.setUriTemplateHandler(new DefaultUriBuilderFactory(baseUrl));
        return rt;
    }

    /**
     * Fournit un client REST préconfiguré pour le microservice patient-service.
     *
     * @param patientsApiUrl URL de l’API patient-service
     * @return client RestTemplate configuré
     */
    @Bean
    public RestTemplate patientApiClient(
            @Value("${patients.api.url}") String patientsApiUrl) {
        return buildClient(patientsApiUrl);
    }

    /**
     * Fournit un client REST préconfiguré pour le microservice note-service.
     *
     * @param notesApiUrl URL de l’API note-service
     * @return client RestTemplate configuré
     */
    @Bean
    public RestTemplate noteApiClient(
            @Value("${notes.api.url}") String notesApiUrl) {
        return buildClient(notesApiUrl);
    }

    /**
     * Fournit un client REST préconfiguré pour le microservice risk-assessment-service.
     *
     * @param riskApiUrl URL de l’API risk-assessment-service
     * @return client RestTemplate configuré
     */
    @Bean
    public RestTemplate riskApiClient(
            @Value("${risk.api.url}") String riskApiUrl) {
        return buildClient(riskApiUrl);
    }
}
