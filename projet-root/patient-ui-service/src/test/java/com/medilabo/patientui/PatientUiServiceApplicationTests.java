package com.medilabo.patientui;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import com.medilabo.patientui.service.PatientService;
import com.medilabo.patientui.service.NoteService;
import com.medilabo.patientui.service.RiskService;

@SpringBootTest(classes = PatientUiServiceApplication.class)
@ActiveProfiles("test")
@TestPropertySource(properties = {
        // --- sécurité / thymeleaf
        "JWT_SECRET=test-secret",
        "spring.thymeleaf.check-template-location=false",
        "server.port=0",

        // --- fournis en minuscules (si ton AppConfig les utilise)
        "patients.api.base=http://gateway.test:8080/api/patients",
        "notes.api.base=http://gateway.test:8080/api/notes",
        "risk.api.base=http://gateway.test:8080/api/risk",

        // --- doublons en MAJUSCULES (ce que ton AppConfig attend actuellement)
        "PATIENT_API_BASE_URL=http://gateway.test:8080/api/patients",
        "NOTE_API_BASE_URL=http://gateway.test:8080/api/notes",
        "RISK_API_BASE_URL=http://gateway.test:8080/api/risk"
})
class PatientUiServiceApplicationTests {

    // on neutralise la sécurité + les appels réseau via des mocks
    @MockBean JwtDecoder jwtDecoder;
    @MockBean PatientService patientService;
    @MockBean NoteService noteService;
    @MockBean RiskService riskService;

    @Test
    void contextLoads() {}
}
