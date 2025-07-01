package com.medilabo.noteservice.repository;

import com.medilabo.noteservice.model.Note;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

/**
 * Interface de repository pour l'accès aux données des {@link Note} dans la base MongoDB.
 * <p>
 * Hérite de {@link MongoRepository} pour fournir les opérations CRUD de base.
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
