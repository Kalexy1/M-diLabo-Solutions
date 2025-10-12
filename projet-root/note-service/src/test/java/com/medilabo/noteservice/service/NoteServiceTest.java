package com.medilabo.noteservice.service;

import com.medilabo.noteservice.model.Note;
import com.medilabo.noteservice.repository.NoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

class NoteServiceTest {

    private NoteService noteService;
    private NoteRepository noteRepository;

    @BeforeEach
    void setUp() {
        noteRepository = mock(NoteRepository.class);
        noteService = new NoteService(noteRepository);
    }

    @Test
    void save_shouldPersistNote_andSetTimestamps() {
        Note note = new Note();
        note.setPatientId(1L);
        note.setContent("Test");

        when(noteRepository.save(any(Note.class))).thenAnswer(inv -> inv.getArgument(0));

        Note result = noteService.save(note);

        assertNotNull(result);
        assertEquals(1L, result.getPatientId());
        assertEquals("Test", result.getContent());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
        verify(noteRepository, times(1)).save(any(Note.class));
    }

    @Test
    void findByPatientId_shouldReturnList() {
        Long patientId = 1L;
        Note n = new Note();
        n.setPatientId(patientId);
        n.setContent("Note A");

        when(noteRepository.findByPatientId(patientId)).thenReturn(List.of(n));

        List<Note> result = noteService.findByPatientId(patientId);

        assertEquals(1, result.size());
        assertEquals("Note A", result.get(0).getContent());
        verify(noteRepository, times(1)).findByPatientId(patientId);
    }

    @Test
    void getById_shouldReturnNote_whenExists() {
        Long id = 10L;
        Note note = new Note();
        note.setId(id);
        note.setContent("Found");

        when(noteRepository.findById(id)).thenReturn(Optional.of(note));

        Note result = noteService.getById(id);

        assertNotNull(result);
        assertEquals("Found", result.getContent());
        verify(noteRepository, times(1)).findById(id);
    }

    @Test
    void getById_shouldThrow_whenNotFound() {
        Long id = 99L;
        when(noteRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> noteService.getById(id));
    }

    @Test
    void update_shouldModifyContent_andTouchUpdatedAt() {
        Long id = 5L;
        Note existing = new Note();
        existing.setId(id);
        existing.setPatientId(9L);
        existing.setContent("Old");
        existing.setCreatedAt(Instant.now().minusSeconds(3600));
        existing.setUpdatedAt(existing.getCreatedAt());

        when(noteRepository.findById(id)).thenReturn(Optional.of(existing));
        when(noteRepository.save(any(Note.class))).thenAnswer(inv -> inv.getArgument(0));

        Note toUpdate = new Note();
        toUpdate.setId(id);
        toUpdate.setContent("New content");

        Note updated = noteService.update(toUpdate);

        assertEquals("New content", updated.getContent());
        assertTrue(updated.getUpdatedAt().isAfter(updated.getCreatedAt()));
        verify(noteRepository, times(1)).findById(id);
        verify(noteRepository, times(1)).save(any(Note.class));
    }

    @Test
    void delete_shouldCallRepositoryDelete() {
        Long id = 123L;

        noteService.delete(id);

        verify(noteRepository, times(1)).deleteById(id);
    }
}
