package com.medilabo.noteservice.repository;

import com.medilabo.noteservice.model.Note;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@ActiveProfiles("test")
class NoteRepositoryTest {

    @Autowired
    private NoteRepository repository;

    @Test
    void shouldSaveAndFindByPatientId() {
        Note note = new Note();
        note.setPatientId(99);
        note.setContenu("Vertiges");

        repository.save(note);

        List<Note> notes = repository.findByPatientId(99);
        assertThat(notes).isNotEmpty();
        assertThat(notes.get(0).getContenu()).isEqualTo("Vertiges");
    }
}
