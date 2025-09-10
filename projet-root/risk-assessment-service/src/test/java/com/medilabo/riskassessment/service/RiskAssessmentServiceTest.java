package com.medilabo.riskassessment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import com.medilabo.riskassessment.dto.NoteDTO;
import com.medilabo.riskassessment.dto.PatientDTO;
import com.medilabo.riskassessment.dto.RiskAssessmentResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;


class RiskAssessmentServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private RiskAssessmentService service;

    private PatientDTO patient;
    private NoteDTO[] notes;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        patient = new PatientDTO();
        patient.setId(1L);
        patient.setNom("Dupont");
        patient.setPrenom("Jean");
        patient.setGenre("M");
        patient.setDateNaissance(LocalDate.parse("1990-01-01"));

        NoteDTO note1 = new NoteDTO();
        note1.setId("1");
        note1.setPatientId(1);
        note1.setContenu("Fumeur et Cholestérol");

        NoteDTO note2 = new NoteDTO();
        note2.setId("2");
        note2.setPatientId(1);
        note2.setContenu("Poids élevé");

        notes = new NoteDTO[] { note1, note2 };
    }

    @Test
    void testAssessRisk_shouldReturnBorderline() {
        when(restTemplate.getForObject("http://patient-service:8081/patients/1", PatientDTO.class))
                .thenReturn(patient);

        when(restTemplate.getForObject("http://note-service:8083/notes/patient/1", NoteDTO[].class))
                .thenReturn(notes);

        String result = service.assessRisk(1L);

        assertThat(result).isEqualTo("Borderline");
    }

    @Test
    void testAssessRiskDetailed_shouldReturnBorderline() {
        when(restTemplate.getForObject("http://patient-service:8081/patients/1", PatientDTO.class))
                .thenReturn(patient);

        when(restTemplate.getForObject("http://note-service:8083/notes/patient/1", NoteDTO[].class))
                .thenReturn(notes);

        RiskAssessmentResponse response = service.assessRiskDetailed(1L);

        assertThat(response.getPatientId()).isEqualTo(1L);
        assertThat(response.getLastName()).isEqualTo("Dupont");
        assertThat(response.getFirstName()).isEqualTo("Jean");
        assertThat(response.getRiskLevel()).isEqualTo("Borderline");
        assertThat(response.getAge()).isGreaterThan(0);
    }

    @Test
    void testNoTriggerMeansNone() {
        NoteDTO emptyNote = new NoteDTO();
        emptyNote.setId("1");
        emptyNote.setPatientId(1);
        emptyNote.setContenu("Aucune mention suspecte");

        NoteDTO[] emptyNotes = new NoteDTO[] { emptyNote };

        when(restTemplate.getForObject("http://patient-service:8081/patients/1", PatientDTO.class))
                .thenReturn(patient);

        when(restTemplate.getForObject("http://note-service:8083/notes/patient/1", NoteDTO[].class))
                .thenReturn(emptyNotes);

        String risk = service.assessRisk(1L);
        assertThat(risk).isEqualTo("None");
    }
}
