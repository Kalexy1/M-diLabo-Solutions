package com.medilabo.riskassessment.service;

import com.medilabo.riskassessment.dto.NoteDTO;
import com.medilabo.riskassessment.dto.PatientDTO;
import com.medilabo.riskassessment.dto.RiskAssessmentResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class RiskAssessmentServiceTest {

    private RiskAssessmentService service;
    private RestTemplate restTemplate;

    @BeforeEach
    void setup() {
        restTemplate = mock(RestTemplate.class);
        service = new RiskAssessmentService(restTemplate);
    }

    @Test
    void shouldReturnNoneWhenNoTriggerAndAgeAbove30() {
        PatientDTO patient = new PatientDTO(1L, "Jean", "Dupont", LocalDate.of(1970, 1, 1), "M");
        NoteDTO[] notes = { new NoteDTO("fatigue sans importance") };

        when(restTemplate.getForObject("http://gateway-service:8080/patients/1", PatientDTO.class))
            .thenReturn(patient);
        when(restTemplate.getForObject("http://gateway-service:8080/notes/patient/1", NoteDTO[].class))
            .thenReturn(notes);

        String result = service.assessRisk(1L);
        assertThat(result).isEqualTo("None");
    }

    @Test
    void shouldReturnEarlyOnsetForManUnder30With5Triggers() {
        PatientDTO patient = new PatientDTO(2L, "Pierre", "Petit", LocalDate.now().minusYears(25), "M");
        NoteDTO[] notes = {
            new NoteDTO("Fumeur, Hémoglobine A1C, Rechute, Anticorps, Cholestérol")
        };

        when(restTemplate.getForObject("http://gateway-service:8080/patients/2", PatientDTO.class))
            .thenReturn(patient);
        when(restTemplate.getForObject("http://gateway-service:8080/notes/patient/2", NoteDTO[].class))
            .thenReturn(notes);

        String result = service.assessRisk(2L);
        assertThat(result).isEqualTo("Early onset");
    }

    @Test
    void shouldReturnDetailedAssessment() {
        PatientDTO patient = new PatientDTO(3L, "Alice", "Durand", LocalDate.of(1980, 5, 10), "F");
        NoteDTO[] notes = {
            new NoteDTO("Hémoglobine A1C et Vertiges"),
            new NoteDTO("Anticorps détectés")
        };

        when(restTemplate.getForObject("http://gateway-service:8080/patients/3", PatientDTO.class))
            .thenReturn(patient);
        when(restTemplate.getForObject("http://gateway-service:8080/notes/patient/3", NoteDTO[].class))
            .thenReturn(notes);

        RiskAssessmentResponse response = service.assessRiskDetailed(3L);

        assertThat(response.getFirstName()).isEqualTo("Alice");
        assertThat(response.getRiskLevel()).isEqualTo("Borderline");
        assertThat(response.getAge()).isGreaterThan(40);
    }

    @Test
    void shouldReturnNoneForOneTriggerAbove30() {
        PatientDTO patient = new PatientDTO(4L, "Marc", "Martin", LocalDate.of(1975, 3, 3), "M");
        NoteDTO[] notes = { new NoteDTO("Cholestérol surveillé") }; // 1 trigger

        when(restTemplate.getForObject("http://gateway-service:8080/patients/4", PatientDTO.class))
            .thenReturn(patient);
        when(restTemplate.getForObject("http://gateway-service:8080/notes/patient/4", NoteDTO[].class))
            .thenReturn(notes);

        assertThat(service.assessRisk(4L)).isEqualTo("None");
    }

    @Test
    void shouldReturnInDangerAbove30With6Triggers() {
        PatientDTO patient = new PatientDTO(5L, "Luc", "Bernard", LocalDate.of(1970, 1, 1), "M");
        NoteDTO[] notes = {
            new NoteDTO("Hémoglobine A1C, Microalbumine, Taille, Poids, Anormal, Cholestérol") // 6
        };

        when(restTemplate.getForObject("http://gateway-service:8080/patients/5", PatientDTO.class))
            .thenReturn(patient);
        when(restTemplate.getForObject("http://gateway-service:8080/notes/patient/5", NoteDTO[].class))
            .thenReturn(notes);

        assertThat(service.assessRisk(5L)).isEqualTo("In Danger");
    }

    @Test
    void shouldReturnEarlyOnsetAbove30With8Triggers() {
        PatientDTO patient = new PatientDTO(6L, "Anne", "Roy", LocalDate.of(1960, 2, 2), "F");
        NoteDTO[] notes = {
            new NoteDTO("Hémoglobine A1C, Microalbumine, Taille, Poids, Fumeur, Anormal, Cholestérol, Vertiges") // 8
        };

        when(restTemplate.getForObject("http://gateway-service:8080/patients/6", PatientDTO.class))
            .thenReturn(patient);
        when(restTemplate.getForObject("http://gateway-service:8080/notes/patient/6", NoteDTO[].class))
            .thenReturn(notes);

        assertThat(service.assessRisk(6L)).isEqualTo("Early onset");
    }

    @Test
    void shouldHandleMaleExactly30Thresholds() {
        PatientDTO p3 = new PatientDTO(7L, "Tom", "B", LocalDate.now().minusYears(30), "M");
        NoteDTO[] n3 = { new NoteDTO("Hémoglobine A1C, Microalbumine, Taille") }; // 3
        when(restTemplate.getForObject("http://gateway-service:8080/patients/7", PatientDTO.class)).thenReturn(p3);
        when(restTemplate.getForObject("http://gateway-service:8080/notes/patient/7", NoteDTO[].class)).thenReturn(n3);
        assertThat(service.assessRisk(7L)).isEqualTo("In Danger");

        PatientDTO p5 = new PatientDTO(8L, "Tom", "B", LocalDate.now().minusYears(30), "M");
        NoteDTO[] n5 = { new NoteDTO("Hémoglobine A1C, Microalbumine, Taille, Poids, Fumeur") }; // 5
        when(restTemplate.getForObject("http://gateway-service:8080/patients/8", PatientDTO.class)).thenReturn(p5);
        when(restTemplate.getForObject("http://gateway-service:8080/notes/patient/8", NoteDTO[].class)).thenReturn(n5);
        assertThat(service.assessRisk(8L)).isEqualTo("Early onset");
    }

    @Test
    void shouldHandleFemaleExactly30Thresholds() {
        PatientDTO p4 = new PatientDTO(9L, "Eva", "C", LocalDate.now().minusYears(30), "F");
        NoteDTO[] n4 = { new NoteDTO("Hémoglobine A1C, Microalbumine, Taille, Poids") }; // 4
        when(restTemplate.getForObject("http://gateway-service:8080/patients/9", PatientDTO.class)).thenReturn(p4);
        when(restTemplate.getForObject("http://gateway-service:8080/notes/patient/9", NoteDTO[].class)).thenReturn(n4);
        assertThat(service.assessRisk(9L)).isEqualTo("In Danger");

        PatientDTO p7 = new PatientDTO(10L, "Eva", "C", LocalDate.now().minusYears(30), "F");
        NoteDTO[] n7 = { new NoteDTO("Hémoglobine A1C, Microalbumine, Taille, Poids, Fumeuse, Anormal, Cholestérol") }; // 7
        when(restTemplate.getForObject("http://gateway-service:8080/patients/10", PatientDTO.class)).thenReturn(p7);
        when(restTemplate.getForObject("http://gateway-service:8080/notes/patient/10", NoteDTO[].class)).thenReturn(n7);
        assertThat(service.assessRisk(10L)).isEqualTo("Early onset");
    }

    @Test
    void shouldHandleMaleUnder30AllPaths() {
        PatientDTO p2 = new PatientDTO(11L, "Leo", "D", LocalDate.now().minusYears(25), "M");
        NoteDTO[] n2 = { new NoteDTO("Hémoglobine A1C, Microalbumine") }; // 2
        when(restTemplate.getForObject("http://gateway-service:8080/patients/11", PatientDTO.class)).thenReturn(p2);
        when(restTemplate.getForObject("http://gateway-service:8080/notes/patient/11", NoteDTO[].class)).thenReturn(n2);
        assertThat(service.assessRisk(11L)).isEqualTo("None");

        PatientDTO p3 = new PatientDTO(12L, "Leo", "D", LocalDate.now().minusYears(25), "M");
        NoteDTO[] n3 = { new NoteDTO("Hémoglobine A1C, Microalbumine, Taille") }; // 3
        when(restTemplate.getForObject("http://gateway-service:8080/patients/12", PatientDTO.class)).thenReturn(p3);
        when(restTemplate.getForObject("http://gateway-service:8080/notes/patient/12", NoteDTO[].class)).thenReturn(n3);
        assertThat(service.assessRisk(12L)).isEqualTo("In Danger");

        PatientDTO p5 = new PatientDTO(13L, "Leo", "D", LocalDate.now().minusYears(25), "M");
        NoteDTO[] n5 = { new NoteDTO("Hémoglobine A1C, Microalbumine, Taille, Poids, Fumeur") }; // 5
        when(restTemplate.getForObject("http://gateway-service:8080/patients/13", PatientDTO.class)).thenReturn(p5);
        when(restTemplate.getForObject("http://gateway-service:8080/notes/patient/13", NoteDTO[].class)).thenReturn(n5);
        assertThat(service.assessRisk(13L)).isEqualTo("Early onset");
    }

    @Test
    void shouldHandleFemaleUnder30AllPaths() {
        PatientDTO p3 = new PatientDTO(14L, "Lia", "E", LocalDate.now().minusYears(25), "F");
        NoteDTO[] n3 = { new NoteDTO("Hémoglobine A1C, Microalbumine, Taille") }; // 3
        when(restTemplate.getForObject("http://gateway-service:8080/patients/14", PatientDTO.class)).thenReturn(p3);
        when(restTemplate.getForObject("http://gateway-service:8080/notes/patient/14", NoteDTO[].class)).thenReturn(n3);
        assertThat(service.assessRisk(14L)).isEqualTo("None");

        PatientDTO p6 = new PatientDTO(15L, "Lia", "E", LocalDate.now().minusYears(25), "F");
        NoteDTO[] n6 = { new NoteDTO("Hémoglobine A1C, Microalbumine, Taille, Poids, Fumeuse, Anormal") }; // 6
        when(restTemplate.getForObject("http://gateway-service:8080/patients/15", PatientDTO.class)).thenReturn(p6);
        when(restTemplate.getForObject("http://gateway-service:8080/notes/patient/15", NoteDTO[].class)).thenReturn(n6);
        assertThat(service.assessRisk(15L)).isEqualTo("In Danger");

        PatientDTO p7 = new PatientDTO(16L, "Lia", "E", LocalDate.now().minusYears(25), "F");
        NoteDTO[] n7 = { new NoteDTO("Hémoglobine A1C, Microalbumine, Taille, Poids, Fumeuse, Anormal, Cholestérol") }; // 7
        when(restTemplate.getForObject("http://gateway-service:8080/patients/16", PatientDTO.class)).thenReturn(p7);
        when(restTemplate.getForObject("http://gateway-service:8080/notes/patient/16", NoteDTO[].class)).thenReturn(n7);
        assertThat(service.assessRisk(16L)).isEqualTo("Early onset");
    }

    @Test
    void shouldReturnNoneForUnknownGenderUnder30EvenWithManyTriggers() {
        PatientDTO patient = new PatientDTO(17L, "X", "Y", LocalDate.now().minusYears(25), "X");
        NoteDTO[] notes = { new NoteDTO("Hémoglobine A1C, Microalbumine, Taille, Poids, Fumeur, Anormal, Cholestérol, Vertiges") }; // 8

        when(restTemplate.getForObject("http://gateway-service:8080/patients/17", PatientDTO.class))
            .thenReturn(patient);
        when(restTemplate.getForObject("http://gateway-service:8080/notes/patient/17", NoteDTO[].class))
            .thenReturn(notes);

        assertThat(service.assessRisk(17L)).isEqualTo("None");
    }

    @Test
    void shouldHandleNullNotesAsNone() {
        PatientDTO patient = new PatientDTO(18L, "Zoe", "K", LocalDate.of(1972, 7, 7), "F");

        when(restTemplate.getForObject("http://gateway-service:8080/patients/18", PatientDTO.class))
            .thenReturn(patient);
        when(restTemplate.getForObject("http://gateway-service:8080/notes/patient/18", NoteDTO[].class))
            .thenReturn(null);

        assertThat(service.assessRisk(18L)).isEqualTo("None");
    }

    @Test
    void shouldIgnoreNullContentNote() {
        PatientDTO patient = new PatientDTO(19L, "Noe", "H", LocalDate.of(1970, 1, 1), "M");
        NoteDTO[] notes = {
            new NoteDTO(null), // ignorée
            new NoteDTO("Hémoglobine A1C, Cholestérol") // 2 -> Borderline pour >30
        };

        when(restTemplate.getForObject("http://gateway-service:8080/patients/19", PatientDTO.class))
            .thenReturn(patient);
        when(restTemplate.getForObject("http://gateway-service:8080/notes/patient/19", NoteDTO[].class))
            .thenReturn(notes);

        assertThat(service.assessRisk(19L)).isEqualTo("Borderline");
    }

    @Test
    void shouldCallExpectedEndpoints() {
        PatientDTO patient = new PatientDTO(20L, "Test", "Urls", LocalDate.of(1970, 1, 1), "M");
        NoteDTO[] notes = { new NoteDTO("Cholestérol") };

        when(restTemplate.getForObject(eq("http://gateway-service:8080/patients/20"), eq(PatientDTO.class)))
            .thenReturn(patient);
        when(restTemplate.getForObject(eq("http://gateway-service:8080/notes/patient/20"), eq(NoteDTO[].class)))
            .thenReturn(notes);

        service.assessRisk(20L);

        verify(restTemplate, times(1))
            .getForObject("http://gateway-service:8080/patients/20", PatientDTO.class);
        verify(restTemplate, times(1))
            .getForObject("http://gateway-service:8080/notes/patient/20", NoteDTO[].class);
        verifyNoMoreInteractions(restTemplate);
    }
}
