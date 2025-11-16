package com.medilabo.patientui;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class PatientUiServiceApplicationTests {

    /**
     * Configuration dédiée aux tests pour résoudre le conflit de RestTemplate.
     *
     * En prod, tu as plusieurs beans RestTemplate (patientApiClient, noteApiClient, riskApiClient).
     * En test, on fournit un RestTemplate marqué @Primary pour que Spring l'injecte
     * dans PatientService / NoteService / RiskService sans lever NoUniqueBeanDefinitionException.
     */
    @TestConfiguration
    static class RestTemplateTestConfig {

        @Bean
        @Primary
        RestTemplate restTemplate() {
            return new RestTemplate();
        }
    }

    @Test
    void contextLoads() {
        // On vérifie simplement que le contexte Spring démarre avec le profil "test".
    }
}
