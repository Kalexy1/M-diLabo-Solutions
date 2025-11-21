package com.medilabo.noteservice.repository;

import com.medilabo.noteservice.model.Note;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Repository MongoDB pour la gestion des entités {@link Note}.
 *
 * <p>Étend {@link MongoRepository} pour fournir les opérations CRUD standard
 * et expose une méthode personnalisée permettant de récupérer les notes
 * associées à un patient donné.</p>
 */
public interface NoteRepository extends MongoRepository<Note, String> {

    /**
     * Recherche toutes les notes liées à un patient.
     *
     * @param patientId identifiant du patient concerné
     * @return liste des notes associées au patient
     */
    List<Note> findByPatientId(Long patientId);
}
