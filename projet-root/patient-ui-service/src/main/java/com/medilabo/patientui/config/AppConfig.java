package com.medilabo.patientui.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class AppConfig {

    private RestTemplate buildClient(String baseUrl) {
        RestTemplate rt = new RestTemplate();
        rt.setUriTemplateHandler(new DefaultUriBuilderFactory(baseUrl));
        return rt;
    }

    @Bean
    public RestTemplate patientApiClient(
            @Value("${patients.api.url}") String patientsApiUrl) {
        return buildClient(patientsApiUrl);
    }

    @Bean
    public RestTemplate noteApiClient(
            @Value("${notes.api.url}") String notesApiUrl) {
        return buildClient(notesApiUrl);
    }

    @Bean
    public RestTemplate riskApiClient(
            @Value("${risk.api.url}") String riskApiUrl) {
        return buildClient(riskApiUrl);
    }
}
