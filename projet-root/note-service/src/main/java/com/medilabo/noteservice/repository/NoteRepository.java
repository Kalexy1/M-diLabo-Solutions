package com.medilabo.noteservice.repository;

import com.medilabo.noteservice.model.Note;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Application: com.medilabo.noteservice.repository
 * <p>
 * Interface <strong>NoteRepository</strong>.
 * <br/>
 * Rôle : Fournit les opérations CRUD et les requêtes spécifiques sur les notes.
 * </p>
 * <p>
 * Hérite de {@link MongoRepository} pour l'accès à MongoDB.
 * </p>
 */
public interface NoteRepository extends MongoRepository<Note, String> {

    /**
     * Recherche toutes les notes associées à un patient en fonction de son identifiant.
     *
     * @param patientId l'identifiant du patient
     * @return une liste de {@link Note} associées à ce patient
     */
    List<Note> findByPatientId(Integer patientId);
}
