package com.medilabo.patientui.config;

import com.medilabo.patientui.controller.PatientController;
import com.medilabo.patientui.repository.PatientRepository;
import com.medilabo.patientui.service.NoteService;
import com.medilabo.patientui.service.PatientService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PatientService patientService;

    @MockBean
    private NoteService noteService;

    @MockBean
    private RestTemplate restTemplate;

    @Test
    @DisplayName("🔒 Accès non autorisé renvoie 403")
    void shouldReturnForbiddenWhenUnauthenticated() throws Exception {
        mockMvc.perform(get("/patients"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ORGANISATEUR")
    void organiserCanAccessPatientsPage() throws Exception {
        Mockito.when(patientService.findAll()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/patients"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void practitionerCanAccessPatientsPage() throws Exception {
        Mockito.when(patientService.findAll()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/patients"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ORGANISATEUR")
    void organiserCannotAccessNotes() throws Exception {
        mockMvc.perform(get("/patients/1/notes"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void practitionerCanAccessNotes() throws Exception {
        Mockito.when(patientService.findById(1L)).thenReturn(java.util.Optional.of(new com.medilabo.patientui.model.Patient()));
        Mockito.when(noteService.getNotesByPatientId(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/patients/1/notes"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ORGANISATEUR")
    void organiserCannotPostNote() throws Exception {
        mockMvc.perform(post("/notes")
                .param("patientId", "1")
                .param("contenu", "test")
                .with(csrf()))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void practitionerCanPostNote() throws Exception {
        mockMvc.perform(post("/notes")
                .param("patientId", "1")
                .param("contenu", "test")
                .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/patients"));
    }

    @Test
    void accessDeniedShouldBePublic() throws Exception {
        mockMvc.perform(get("/access-denied"))
            .andExpect(status().isOk());
    }
}
