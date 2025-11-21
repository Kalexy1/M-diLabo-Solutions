package com.medilabo.patientui.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

class AppConfigTest {

    private final AppConfig config = new AppConfig();

    @Test
    void patientApiClient_shouldCreateRestTemplate() {
        RestTemplate client = config.patientApiClient("http://gateway:8080/api/patients");
        assertThat(client).isNotNull();
    }

    @Test
    void noteApiClient_shouldCreateRestTemplate() {
        RestTemplate client = config.noteApiClient("http://gateway:8080/api/notes");
        assertThat(client).isNotNull();
    }

    @Test
    void riskApiClient_shouldCreateRestTemplate() {
        RestTemplate client = config.riskApiClient("http://gateway:8080/api/risk");
        assertThat(client).isNotNull();
    }
}
