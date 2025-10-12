package com.medilabo.riskassessment;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.oauth2.jwt.JwtDecoder;

@SpringBootTest(classes = RiskAssessmentServiceApplication.class)
@ActiveProfiles("test")
@TestPropertySource(properties = {
        // Sécurise la valeur pour éviter le placeholder manquant si la config l’évalue quand même
        "JWT_SECRET=test-secret",
        // Cohérent avec ton service corrigé
        "PATIENT_API_BASE_URL=http://gateway-service:8080/api/patients",
        "NOTE_API_BASE_URL=http://gateway-service:8080/api/notes/patient",
        // Evite le port fixe pendant les tests
        "server.port=0"
})
class RiskAssessmentServiceApplicationTests {

    // Évite tout appel réseau et satisfait le constructor de RiskAssessmentService
    @MockBean
    private RestTemplate restTemplate;

    // Remplace le bean jwtDecoder pour que la config sécurité n’essaie pas de créer le sien
    @MockBean
    private JwtDecoder jwtDecoder;

    @Test
    void contextLoads() {
    }
}
