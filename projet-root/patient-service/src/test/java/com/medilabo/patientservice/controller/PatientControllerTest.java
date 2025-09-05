package com.medilabo.patientservice.controller;

import com.medilabo.patientservice.model.Patient;
import com.medilabo.patientservice.service.PatientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

    @Test
    @WithMockUser(roles = "ORGANISATEUR")
    void shouldRedirectListToUi() throws Exception {
        mockMvc.perform(get("/patients"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ui/patients"));
    }

    @Test
    @WithMockUser(roles = "ORGANISATEUR")
    void shouldRedirectAddFormToUi() throws Exception {
        mockMvc.perform(get("/patients/new"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ui/patients/new"));
    }

    @Test
    @WithMockUser(roles = "ORGANISATEUR")
    void shouldSavePatientAndRedirectToUi() throws Exception {
        mockMvc.perform(post("/patients")
                        .with(csrf())
                        .param("nom", "Doe")
                        .param("prenom", "John")
                        .param("dateNaissance", "1990-01-01")
                        .param("genre", "M"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ui/patients"));
        verify(patientService).savePatient(any(Patient.class));
    }

    @Test
    @WithMockUser(roles = "ORGANISATEUR")
    void shouldRedirectEditFormToUi_whenPatientExists() throws Exception {
        when(patientService.getPatientById(1L)).thenReturn(Optional.of(mock(Patient.class)));

        mockMvc.perform(get("/patients/edit/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ui/patients/edit/1"));
    }

    @Test
    @WithMockUser(roles = "ORGANISATEUR")
    void shouldUpdatePatientAndRedirectToUi() throws Exception {
        mockMvc.perform(post("/patients/update")
                        .with(csrf())
                        .param("id", "1")
                        .param("nom", "Doe")
                        .param("prenom", "Jane")
                        .param("dateNaissance", "1992-02-02")
                        .param("genre", "F"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ui/patients"));
        verify(patientService).updatePatient(eq(1L), any(Patient.class));
        verify(patientService, never()).savePatient(any(Patient.class));
    }

    @Test
    @WithMockUser(roles = "ORGANISATEUR")
    void shouldDeletePatientAndRedirectToUi() throws Exception {
        mockMvc.perform(post("/patients/delete/1").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ui/patients"));
        verify(patientService).deletePatient(1L);
    }
}
