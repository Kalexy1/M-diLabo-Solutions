package com.medilabo.patientui.controller;

import com.medilabo.patientui.model.Patient;
import com.medilabo.patientui.service.NoteService;
import com.medilabo.patientui.service.PatientService;
import com.medilabo.patientui.dto.RiskAssessmentResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PatientController.class)
@AutoConfigureMockMvc(addFilters = false)
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PatientService patientService;

    @MockBean
    private NoteService noteService;

    @MockBean
    private RestTemplate restTemplate;

    private Patient patient;
    private RiskAssessmentResponse risk;

    @BeforeEach
    void setup() {
        patient = new Patient();
        patient.setId(1L);
        patient.setNom("Dupont");
        patient.setPrenom("Jean");
        patient.setGenre("M");
        patient.setDateNaissance(LocalDate.of(1980, 1, 1));

        risk = new RiskAssessmentResponse();
        risk.setAge(44);
        risk.setRiskLevel("None");
    }

    @Test
    void shouldDisplayPatientList() throws Exception {
        when(patientService.getAllPatients()).thenReturn(List.of(patient));
        when(noteService.getNotesByPatientId(1L)).thenReturn(Collections.emptyList());
        when(restTemplate.getForObject(anyString(), eq(RiskAssessmentResponse.class))).thenReturn(risk);

        mockMvc.perform(get("/patients"))
                .andExpect(status().isOk())
                .andExpect(view().name("patients"))
                .andExpect(model().attributeExists("patients", "notesByPatient", "riskByPatient"));
    }

    @Test
    void shouldShowAddForm() throws Exception {
        mockMvc.perform(get("/patients/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-patient"))
                .andExpect(model().attributeExists("patient"));
    }

    @Test
    void shouldCreatePatient() throws Exception {
        mockMvc.perform(post("/patients")
		                .param("nom", "Dupont")
		                .param("prenom", "Jean")
		                .param("dateNaissance", "1980-01-01")
		                .param("genre", "M"))
		        .andExpect(status().is3xxRedirection())
		        .andExpect(redirectedUrl("/patients"));

        verify(patientService).createPatient(ArgumentMatchers.any(Patient.class));
	}

    @Test
    void shouldShowEditForm() throws Exception {
    	when(patientService.getPatientById(1L)).thenReturn(Optional.of(patient));

        mockMvc.perform(get("/patients/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("edit-patient"))
                .andExpect(model().attributeExists("patient"));
    }

    @Test
    void shouldUpdatePatient() throws Exception {
        mockMvc.perform(post("/patients/update")
		                .param("id", "1")
		                .param("nom", "Dupont")
		                .param("prenom", "Jean")
		                .param("dateNaissance", "1980-01-01")
		                .param("genre", "M"))
		        .andExpect(status().is3xxRedirection())
		        .andExpect(redirectedUrl("/patients"));

        verify(patientService).updatePatient(eq(1L), ArgumentMatchers.any(Patient.class));
	}

    @Test
    void shouldShowPatientNotes() throws Exception {
    	when(patientService.getPatientById(1L)).thenReturn(Optional.of(patient));
        when(noteService.getNotesByPatientId(1L)).thenReturn(List.of(Map.of("contenu", "note")));

        mockMvc.perform(get("/patients/1/notes"))
                .andExpect(status().isOk())
                .andExpect(view().name("patient-notes"))
                .andExpect(model().attributeExists("patient", "notes"));
    }

    @Test
    void shouldShowRiskReport() throws Exception {
    	when(patientService.getPatientById(1L)).thenReturn(Optional.of(patient));
        when(restTemplate.getForObject(anyString(), eq(RiskAssessmentResponse.class))).thenReturn(risk);

        mockMvc.perform(get("/patients/1/risk"))
                .andExpect(status().isOk())
                .andExpect(view().name("risk-report"))
                .andExpect(model().attributeExists("patient", "risk"));
    }

    @Test
    void shouldDeletePatient() throws Exception {
        mockMvc.perform(get("/patients/delete/1"))
		        .andExpect(status().is3xxRedirection())
		        .andExpect(redirectedUrl("/patients"));

        verify(patientService).deletePatient(1L);
    }

    @Test
    void shouldAddNote() throws Exception {
        mockMvc.perform(post("/notes")
		                .param("patientId", "1")
		                .param("contenu", "note test"))
		        .andExpect(status().is3xxRedirection())
		        .andExpect(redirectedUrl("/patients"));

        verify(noteService).ajouterNote(1L, "note test");
    }

    @Test
    void shouldGeneratePdfReport() throws Exception {
    	when(patientService.getPatientById(1L)).thenReturn(Optional.of(patient));
        when(restTemplate.getForObject(anyString(), eq(RiskAssessmentResponse.class))).thenReturn(risk);

        MockHttpServletResponse response = new MockHttpServletResponse();

        PatientController controller = new PatientController(patientService, noteService, restTemplate);
        controller.downloadPdfReport(1L, response);

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getContentType()).isEqualTo("application/pdf");
        assertThat(response.getContentAsByteArray()).isNotEmpty();
    }
}
