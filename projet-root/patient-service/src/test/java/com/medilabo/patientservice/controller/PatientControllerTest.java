package com.medilabo.patientservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.medilabo.patientservice.model.Patient;
import com.medilabo.patientservice.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PatientRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    private Patient testPatient;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        testPatient = new Patient();
        testPatient.setPrenom("Alice");
        testPatient.setNom("Durand");
        testPatient.setGenre("F");
        testPatient.setDateNaissance(LocalDate.of(1995, 3, 15));
        testPatient.setAdresse("7 rue du Test");
        testPatient.setTelephone("0123456789");

        testPatient = repository.save(testPatient);
    }

    @Test
    void testGetAll() throws Exception {
        mockMvc.perform(get("/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nom").value("Durand"));
    }

    @Test
    void testGetOne_found() throws Exception {
        mockMvc.perform(get("/patients/" + testPatient.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prenom").value("Alice"));
    }

    @Test
    void testGetOne_notFound() throws Exception {
        mockMvc.perform(get("/patients/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreate() throws Exception {
        Patient newPatient = new Patient();
        newPatient.setPrenom("Bob");
        newPatient.setNom("Martin");
        newPatient.setGenre("M");
        newPatient.setDateNaissance(LocalDate.of(1988, 5, 12));
        newPatient.setAdresse("99 avenue test");
        newPatient.setTelephone("0611223344");

        mockMvc.perform(post("/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newPatient)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Martin"));
    }

    @Test
    void testUpdate_found() throws Exception {
        testPatient.setAdresse("Nouvelle adresse");

        mockMvc.perform(put("/patients/" + testPatient.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testPatient)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.adresse").value("Nouvelle adresse"));
    }

    @Test
    void testUpdate_notFound() throws Exception {
        mockMvc.perform(put("/patients/9999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testPatient)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDelete_found() throws Exception {
        mockMvc.perform(delete("/patients/" + testPatient.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void testDelete_notFound() throws Exception {
        mockMvc.perform(delete("/patients/9999"))
                .andExpect(status().isNotFound());
    }
}
