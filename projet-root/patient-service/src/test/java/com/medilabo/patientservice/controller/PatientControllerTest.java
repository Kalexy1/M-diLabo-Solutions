package com.medilabo.patientservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medilabo.patientservice.model.Patient;
import com.medilabo.patientservice.service.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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

    @Autowired
    private ObjectMapper mapper;

    private Patient sample;

    @BeforeEach
    void setup() {
        mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        sample = new Patient();
        sample.setId(1L);
        sample.setFirstName("Marie");
        sample.setLastName("Curie");
        sample.setBirthDate(LocalDate.of(1867, 11, 7));
        sample.setGender("F");
        sample.setAddress("Paris");
        sample.setPhone("0102030405");
    }

    // ---------- GET /api/patients ----------

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void findAll_shouldReturnListOfPatients() throws Exception {
        when(patientService.findAll()).thenReturn(List.of(sample));

        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].firstName").value("Marie"))
                .andExpect(jsonPath("$[0].lastName").value("Curie"));

        verify(patientService).findAll();
    }

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void findAll_shouldSearchByLastName_whenQueryProvided() throws Exception {
        when(patientService.searchByLastName("Curie")).thenReturn(List.of(sample));

        mockMvc.perform(get("/api/patients?q=Curie"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].lastName").value("Curie"));

        verify(patientService).searchByLastName("Curie");
        verify(patientService, never()).findAll();
    }

    // ---------- GET /api/patients/{id} ----------

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void getOne_shouldReturnPatient_whenFound() throws Exception {
        when(patientService.getById(1L)).thenReturn(sample);

        mockMvc.perform(get("/api/patients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Marie"))
                .andExpect(jsonPath("$.lastName").value("Curie"))
                .andExpect(jsonPath("$.gender").value("F"));

        verify(patientService).getById(1L);
    }

    // ---------- POST /api/patients ----------

    @Test
    @WithMockUser(roles = "ORGANISATEUR")
    void create_shouldSaveAndReturnPatient_with201() throws Exception {
        when(patientService.create(any(Patient.class))).thenReturn(sample);

        Patient req = new Patient();
        req.setFirstName("Marie");
        req.setLastName("Curie");
        req.setBirthDate(LocalDate.of(1867, 11, 7));
        req.setGender("F");
        req.setAddress("Paris");
        req.setPhone("0102030405");

        mockMvc.perform(post("/api/patients")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.lastName").value("Curie"));

        ArgumentCaptor<Patient> captor = ArgumentCaptor.forClass(Patient.class);
        verify(patientService).create(captor.capture());
        assertThat(captor.getValue().getFirstName()).isEqualTo("Marie");
        assertThat(captor.getValue().getId()).isNull();
    }

    // ---------- PUT /api/patients/{id} ----------

    @Test
    @WithMockUser(roles = "ORGANISATEUR")
    void update_shouldModifyAndReturnUpdatedPatient() throws Exception {
        Patient updated = new Patient();
        updated.setId(1L);
        updated.setFirstName("Marie-Sklodowska");
        updated.setLastName("Curie");
        updated.setBirthDate(LocalDate.of(1867, 11, 7));
        updated.setGender("F");
        updated.setAddress("Paris");
        updated.setPhone("0102030405");

        when(patientService.update(eq(1L), any(Patient.class))).thenReturn(updated);

        mockMvc.perform(put("/api/patients/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Marie-Sklodowska"))
                .andExpect(jsonPath("$.lastName").value("Curie"));

        verify(patientService).update(eq(1L), any(Patient.class));
    }

    // ---------- DELETE /api/patients/{id} ----------

    @Test
    @WithMockUser(roles = "ORGANISATEUR")
    void delete_shouldReturn204() throws Exception {
        doNothing().when(patientService).delete(1L);

        mockMvc.perform(delete("/api/patients/1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(patientService).delete(1L);
    }
}
