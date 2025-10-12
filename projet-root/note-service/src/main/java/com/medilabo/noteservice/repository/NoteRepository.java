package com.medilabo.noteservice.repository;

import com.medilabo.noteservice.model.Note;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Interface NoteRepository : CRUD + requêtes spécifiques sur les notes.
 */
public interface NoteRepository extends MongoRepository<Note, Long> {

    /**
     * Recherche toutes les notes d'un patient.
     * @param patientId identifiant du patient (Long)
     */
    List<Note> findByPatientId(Long patientId);
}
