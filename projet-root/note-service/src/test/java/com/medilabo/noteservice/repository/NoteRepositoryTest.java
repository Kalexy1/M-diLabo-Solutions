package com.medilabo.noteservice.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

import com.medilabo.noteservice.model.Note;

@DataMongoTest
@ActiveProfiles("test")
class NoteRepositoryTest {

    @Autowired
    private NoteRepository noteRepository;

    @BeforeEach
    public void cleanDatabase() {
        noteRepository.deleteAll();
    }

    @Test
    void shouldSaveAndFindByPatientId() {
        Note note = new Note();
        note.setPatientId(99);
        note.setContenu("Vertiges");

        noteRepository.save(note);

        List<Note> notes = noteRepository.findByPatientId(99);
        assertThat(notes).isNotEmpty();
        assertThat(notes.get(0).getContenu()).isEqualTo("Vertiges");
    }

    @Test
    void shouldReturnEmptyListWhenNoNoteExists() {
        List<Note> notes = noteRepository.findByPatientId(12345);
        assertThat(notes).isEmpty();
    }

    @Test
    void shouldSaveMultipleNotesForSamePatient() {
        Note note1 = new Note();
        note1.setPatientId(77);
        note1.setContenu("Première note");

        Note note2 = new Note();
        note2.setPatientId(77);
        note2.setContenu("Deuxième note");

        noteRepository.save(note1);
        noteRepository.save(note2);

        List<Note> notes = noteRepository.findByPatientId(77);
        assertThat(notes).hasSize(2);
    }

    @Test
    void shouldGenerateIdWhenSaved() {
        Note note = new Note();
        note.setPatientId(42);
        note.setContenu("Contenu test");

        Note saved = noteRepository.save(note);

        assertThat(saved.getId()).isNotNull();
    }
}
