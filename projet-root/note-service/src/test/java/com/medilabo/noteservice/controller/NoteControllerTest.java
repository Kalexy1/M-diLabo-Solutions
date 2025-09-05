package com.medilabo.noteservice.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.medilabo.noteservice.model.Note;
import com.medilabo.noteservice.service.NoteService;

@WebMvcTest(NoteController.class)
@Import(com.medilabo.noteservice.config.NoteServiceSecurityConfig.class)
class NoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NoteService noteService;

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void getNotesByPatient_shouldReturnList() throws Exception {
        Mockito.when(noteService.getNotesByPatientId(1)).thenReturn(List.of(new Note()));

        mockMvc.perform(get("/notes/patient/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void getNotesByPatient_shouldReturnNoContent() throws Exception {
        Mockito.when(noteService.getNotesByPatientId(2)).thenReturn(List.of());

        mockMvc.perform(get("/notes/patient/2"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void addNote_shouldReturnCreated() throws Exception {
        String noteJson = """
            {
              "patientId": 1,
              "content": "Test content"
            }
            """;

        mockMvc.perform(post("/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(noteJson))
                .andExpect(status().isCreated());
    }
}
