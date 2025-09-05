package com.medilabo.patientui.controller;

import com.medilabo.patientui.dto.RiskAssessmentResponse;
import com.medilabo.patientui.model.Patient;
import com.medilabo.patientui.service.NoteService;
import com.medilabo.patientui.service.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PatientController.class)
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
    @WithMockUser(roles = {"ORGANISATEUR"})
    void shouldDisplayPatientList() throws Exception {
        when(patientService.findAll()).thenReturn(List.of(patient));
        when(noteService.getNotesByPatientId(1L)).thenReturn(Collections.emptyList());
        when(restTemplate.getForObject(anyString(), eq(RiskAssessmentResponse.class))).thenReturn(risk);

        mockMvc.perform(get("/patients"))
                .andExpect(status().isOk())
                .andExpect(view().name("patients"))
                .andExpect(model().attributeExists("patients", "notesByPatient", "riskByPatient"));
    }

    @Test
    @WithMockUser(roles = {"ORGANISATEUR"})
    void shouldShowAddForm() throws Exception {
        mockMvc.perform(get("/patients/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-patient"))
                .andExpect(model().attributeExists("patient"));
    }

    @Test
    @WithMockUser(roles = {"ORGANISATEUR"})
    void shouldCreatePatient() throws Exception {
        mockMvc.perform(post("/patients")
                        .param("nom", "Dupont")
                        .param("prenom", "Jean")
                        .param("dateNaissance", "1980-01-01")
                        .param("genre", "M")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/patients"));
    }

    @Test
    @WithMockUser(roles = {"ORGANISATEUR"})
    void shouldShowEditForm() throws Exception {
        when(patientService.findById(1L)).thenReturn(Optional.of(patient));

        mockMvc.perform(get("/patients/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("edit-patient"))
                .andExpect(model().attributeExists("patient"));
    }

    @Test
    @WithMockUser(roles = {"ORGANISATEUR"})
    void shouldUpdatePatient() throws Exception {
        mockMvc.perform(post("/patients/update")
                        .param("id", "1")
                        .param("nom", "Dupont")
                        .param("prenom", "Jean")
                        .param("dateNaissance", "1980-01-01")
                        .param("genre", "M")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/patients"));
    }

    @Test
    @WithMockUser(roles = {"PRATICIEN"})
    void shouldShowPatientNotes() throws Exception {
        when(patientService.findById(1L)).thenReturn(Optional.of(patient));
        when(noteService.getNotesByPatientId(1L)).thenReturn(List.of(Map.of("contenu", "note")));

        mockMvc.perform(get("/patients/1/notes"))
                .andExpect(status().isOk())
                .andExpect(view().name("patient-notes"))
                .andExpect(model().attributeExists("patient", "notes"));
    }

    @Test
    @WithMockUser(roles = {"PRATICIEN"})
    void shouldShowRiskReport() throws Exception {
        when(patientService.findById(1L)).thenReturn(Optional.of(patient));
        when(restTemplate.getForObject(anyString(), eq(RiskAssessmentResponse.class))).thenReturn(risk);

        mockMvc.perform(get("/patients/1/risk"))
                .andExpect(status().isOk())
                .andExpect(view().name("risk-report"))
                .andExpect(model().attributeExists("patient", "risk"));
    }

    @Test
    @WithMockUser(roles = {"ORGANISATEUR"})
    void shouldDeletePatient_postWithCsrf() throws Exception {
        mockMvc.perform(post("/patients/delete/1").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/patients"));

        verify(patientService).deleteById(1L);
    }

    @Test
    @WithMockUser(roles = {"PRATICIEN"})
    void shouldAddNote_postWithCsrf() throws Exception {
        mockMvc.perform(post("/notes")
                        .param("patientId", "1")
                        .param("contenu", "note test")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/patients"));

        verify(noteService).ajouterNote(1L, "note test");
    }

    @Test
    @WithMockUser(roles = {"PRATICIEN"})
    void shouldGeneratePdfReport() throws Exception {
        when(patientService.findById(1L)).thenReturn(Optional.of(patient));
        when(restTemplate.getForObject(anyString(), eq(RiskAssessmentResponse.class))).thenReturn(risk);

        MockHttpServletResponse response = new MockHttpServletResponse();

        PatientController controller = new PatientController(patientService, noteService, restTemplate);
        controller.downloadPdfReport(1L, response);

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getContentType()).isEqualTo("application/pdf");
        assertThat(response.getContentAsByteArray()).isNotEmpty();
    }
}
