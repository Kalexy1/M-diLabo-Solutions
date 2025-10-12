package com.medilabo.patientui.config;

import com.medilabo.patientui.controller.PatientController;
import com.medilabo.patientui.model.Patient;
import com.medilabo.patientui.service.NoteService;
import com.medilabo.patientui.service.PatientService;
import com.medilabo.patientui.service.RiskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PatientController.class)
class SecurityConfigTest {

    @Autowired MockMvc mvc;

    @MockBean JwtDecoder jwtDecoder;

    @MockBean PatientService patientService;
    @MockBean NoteService noteService;
    @MockBean RiskService riskService;

    @BeforeEach
    void setup() {
        given(patientService.findAll(anyString())).willReturn(List.of());

        Patient saved = new Patient();
        saved.setId(42L);
        given(patientService.create(any(Patient.class), anyString())).willReturn(saved);
    }

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void praticien_canGET_patients_and_POST_results_in_3xx() throws Exception {
        mvc.perform(get("/ui/patients"))
           .andExpect(status().isOk());

        mvc.perform(post("/ui/patients").with(csrf()))
           .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(roles = "ORGANISATEUR")
    void organisateur_post_redirects_to_patients_list_in_this_setup() throws Exception {
        mvc.perform(post("/ui/patients").with(csrf())
                .param("firstName","Bob")
                .param("lastName","Martin"))
           .andExpect(status().is3xxRedirection())
           .andExpect(redirectedUrl("/ui/patients"));
    }

    @Test
    void unauthenticated_user_gets_401_on_ui() throws Exception {
        mvc.perform(get("/ui/patients"))
           .andExpect(status().isUnauthorized());
    }
}
