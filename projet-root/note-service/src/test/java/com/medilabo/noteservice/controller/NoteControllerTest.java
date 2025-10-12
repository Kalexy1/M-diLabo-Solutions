package com.medilabo.noteservice.controller;

import com.medilabo.noteservice.model.Note;
import com.medilabo.noteservice.service.NoteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NoteController.class)
class NoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NoteService noteService;

    private Note sample;

    @BeforeEach
    void setup() {
        sample = new Note();
        sample.setId(1L);
        sample.setPatientId(99L);
        sample.setContent("Vertiges");
        sample.setCreatedAt(Instant.now());
        sample.setUpdatedAt(Instant.now());
    }

    @Test
    @WithMockUser
    void findByPatient_shouldReturnList() throws Exception {
        when(noteService.findByPatientId(99L)).thenReturn(List.of(sample));

        mockMvc.perform(get("/api/notes/patient/99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("Vertiges"));

        verify(noteService, times(1)).findByPatientId(99L);
    }

    @Test
    @WithMockUser
    void getOne_shouldReturnNote() throws Exception {
        when(noteService.getById(1L)).thenReturn(sample);

        mockMvc.perform(get("/api/notes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Vertiges"))
                .andExpect(jsonPath("$.patientId").value(99));

        verify(noteService, times(1)).getById(1L);
    }

    @Test
    @WithMockUser
    void create_shouldSaveNote_andReturnCreated() throws Exception {
        when(noteService.save(any(Note.class))).thenReturn(sample);

        mockMvc.perform(post("/api/notes/patient/99")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"Nouvelle note\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("Vertiges"));

        var captor = ArgumentCaptor.forClass(Note.class);
        verify(noteService).save(captor.capture());
        assertThat(captor.getValue().getPatientId()).isEqualTo(99L);
        assertThat(captor.getValue().getId()).isNull();
    }

    @Test
    @WithMockUser
    void update_shouldModifyNote() throws Exception {
        when(noteService.update(any(Note.class))).thenReturn(sample);

        mockMvc.perform(put("/api/notes/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"Mise à jour\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Vertiges"));

        var captor = ArgumentCaptor.forClass(Note.class);
        verify(noteService).update(captor.capture());
        assertThat(captor.getValue().getId()).isEqualTo(1L);
    }

    @Test
    @WithMockUser
    void delete_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/notes/1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(noteService, times(1)).delete(1L);
    }
}
