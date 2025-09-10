package com.medilabo.noteservice.service;

import com.medilabo.noteservice.model.Note;
import com.medilabo.noteservice.repository.NoteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NoteServiceTest {

    @Mock
    private NoteRepository noteRepository;

    @InjectMocks
    private NoteService noteService;

    @Test
    void getNotesByPatient_returnsNotes() {
        List<Note> notes = Arrays.asList(new Note(), new Note());
        when(noteRepository.findByPatientId(1)).thenReturn(notes);

        List<Note> result = noteService.getNotesByPatient(1);

        assertThat(result).hasSize(2);
        verify(noteRepository).findByPatientId(1);
    }

    @Test
    void addNote_savesNote() {
        Note note = new Note();
        when(noteRepository.save(note)).thenReturn(note);

        Note result = noteService.addNote(note);

        assertThat(result).isEqualTo(note);
        verify(noteRepository).save(note);
    }

    @Test
    void deleteAll_removesAllNotes() {
        noteService.deleteAll();

        verify(noteRepository).deleteAll();
    }
}