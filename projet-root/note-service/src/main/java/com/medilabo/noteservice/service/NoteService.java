package com.medilabo.noteservice.service;

import com.medilabo.noteservice.model.Note;
import com.medilabo.noteservice.repository.NoteRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/**
 * Service gérant la logique métier liée aux notes médicales.
 * <p>
 * Cette classe interagit avec le {@link NoteRepository} pour effectuer les
 * opérations CRUD sur les notes des patients, notamment la création,
 * la mise à jour, la suppression et la recherche par patient.
 * </p>
 */
@Service
public class NoteService {

    /**
     * Référentiel de gestion des notes.
     */
    private final NoteRepository repo;

    /**
     * Crée une instance du service de gestion des notes.
     *
     * @param repo le repository permettant l’accès aux données des notes
     */
    public NoteService(NoteRepository repo) {
        this.repo = repo;
    }

    /**
     * Récupère la liste des notes associées à un patient.
     *
     * @param patientId l’identifiant du patient
     * @return la liste des notes du patient
     */
    public List<Note> findByPatientId(Long patientId) {
        return repo.findByPatientId(patientId);
    }

    /**
     * Récupère une note à partir de son identifiant.
     *
     * @param id l’identifiant de la note
     * @return la note correspondante
     * @throws IllegalArgumentException si la note n’existe pas
     */
    public Note getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Note introuvable: " + id));
    }

    /**
     * Crée et enregistre une nouvelle note.
     * <p>
     * Initialise les champs {@code createdAt} et {@code updatedAt}
     * à la date et l’heure actuelles.
     * </p>
     *
     * @param n la note à enregistrer
     * @return la note sauvegardée
     */
    public Note save(Note n) {
        var now = Instant.now();
        n.setCreatedAt(now);
        n.setUpdatedAt(now);
        return repo.save(n);
    }

    /**
     * Met à jour une note existante.
     * <p>
     * Seules les informations de contenu et la date de mise à jour sont modifiées.
     * </p>
     *
     * @param n la note contenant les modifications
     * @return la note mise à jour
     * @throws IllegalArgumentException si la note n’existe pas
     */
    public Note update(Note n) {
        var existing = getById(n.getId());
        existing.setContent(n.getContent());
        existing.setUpdatedAt(Instant.now());
        return repo.save(existing);
    }

    /**
     * Supprime une note à partir de son identifiant.
     *
     * @param id l’identifiant de la note à supprimer
     */
    public void delete(Long id) {
        repo.deleteById(id);
    }
}
