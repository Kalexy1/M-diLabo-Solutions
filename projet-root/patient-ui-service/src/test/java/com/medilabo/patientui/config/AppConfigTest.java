package com.medilabo.patientui.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;

class AppConfigTest {

    private final AppConfig config = new AppConfig();

    @Test
    void patientApiClient_shouldCreateWebClient() {
        WebClient client = config.patientApiClient("http://gateway:8080/api/patients");
        assertThat(client).isNotNull();
    }

    @Test
    void noteApiClient_shouldCreateWebClient() {
        WebClient client = config.noteApiClient("http://gateway:8080/api/notes");
        assertThat(client).isNotNull();
    }

    @Test
    void riskApiClient_shouldCreateWebClient() {
        WebClient client = config.riskApiClient("http://gateway:8080/api/risk");
        assertThat(client).isNotNull();
    }
}
