package com.medilabo.noteservice.repository;

import com.medilabo.noteservice.model.Note;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Référentiel MongoDB pour l’entité {@link Note}.
 * <p>
 * Fournit les opérations CRUD standard ainsi que des méthodes de requête
 * personnalisées pour interagir avec la collection {@code notes}.
 * </p>
 */
public interface NoteRepository extends MongoRepository<Note, Long> {

    /**
     * Recherche toutes les notes associées à un patient spécifique.
     *
     * @param patientId l’identifiant du patient
     * @return la liste des notes appartenant à ce patient
     */
    List<Note> findByPatientId(Long patientId);
}
