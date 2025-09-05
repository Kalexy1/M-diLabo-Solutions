package com.medilabo.noteservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.medilabo.noteservice.model.Note;
import com.medilabo.noteservice.repository.NoteRepository;

class NoteServiceTest {

    private NoteService noteService;
    private NoteRepository noteRepository;

    @BeforeEach
    void setUp() {
        noteRepository = mock(NoteRepository.class);
        noteService = new NoteService(noteRepository);
    }

    @Test
    void addNote_shouldSaveNote() {
        Note note = new Note(null, "Test");
        when(noteRepository.save(note)).thenReturn(note);

        Note result = noteService.addNote(note);

        assertNotNull(result);
        verify(noteRepository, times(1)).save(note);
    }

    @Test
    void getNotesByPatientId_shouldReturnList() {
        int patientId = 1;
        List<Note> mockNotes = List.of(new Note(null, "Note A"));
        when(noteRepository.findByPatientId(patientId)).thenReturn(mockNotes);

        List<Note> result = noteService.getNotesByPatientId(patientId);

        assertEquals(1, result.size());
        verify(noteRepository, times(1)).findByPatientId(patientId);
    }
    
    @Test
    void deleteNoteById_shouldCallRepositoryDelete() {
        String noteId = "123abc";

        noteService.deleteNoteById(noteId);

        verify(noteRepository, times(1)).deleteById(noteId);
    }


}
