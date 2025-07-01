package com.medilabo.noteservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medilabo.noteservice.model.Note;
import com.medilabo.noteservice.repository.NoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class NoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NoteRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    private Note note;

    @BeforeEach
    void setup() {
        repository.deleteAll(); // Nettoyage
        Note newNote = new Note();
        newNote.setPatientId(42);
        newNote.setContenu("Microalbumine détectée");
        note = repository.save(newNote); // On garde la version avec ID
    }

    @Test
    void testFindByPatientId() throws Exception {
        mockMvc.perform(get("/notes/patient/" + note.getPatientId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].contenu").value("Microalbumine détectée"));
    }

    @Test
    void testAddNote() throws Exception {
        Note newNote = new Note();
        newNote.setPatientId(99);
        newNote.setContenu("Cholestérol élevé");

        mockMvc.perform(post("/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newNote)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contenu").value("Cholestérol élevé"));
    }

}
