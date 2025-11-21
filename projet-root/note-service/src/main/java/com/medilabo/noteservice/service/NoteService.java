package com.medilabo.noteservice.service;

import com.medilabo.noteservice.model.Note;
import com.medilabo.noteservice.repository.NoteRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/**
 * Service métier du microservice NoteService.
 *
 * <p>Gère les opérations CRUD sur les notes médicales et applique la logique
 * associée à la création, la mise à jour et la suppression de notes.</p>
 */
@Service
public class NoteService {

    private final NoteRepository repo;

    /**
     * Construit le service de gestion des notes.
     *
     * @param repo repository MongoDB pour l'entité {@link Note}
     */
    public NoteService(NoteRepository repo) {
        this.repo = repo;
    }

    /**
     * Recherche toutes les notes associées à un patient.
     *
     * @param patientId identifiant du patient
     * @return liste des notes associées
     */
    public List<Note> findByPatientId(Long patientId) {
        return repo.findByPatientId(patientId);
    }

    /**
     * Récupère une note à partir de son identifiant.
     *
     * @param id identifiant de la note
     * @return la note correspondante
     * @throws IllegalArgumentException si aucune note n'est trouvée
     */
    public Note getById(String id) {
        return repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Note introuvable: " + id));
    }

    /**
     * Crée et enregistre une nouvelle note.
     *
     * <p>La date de création et de mise à jour est initialisée à l'instant
     * courant et l'identifiant est volontairement laissé à {@code null}
     * afin que MongoDB en génère un automatiquement.</p>
     *
     * @param n note à enregistrer
     * @return la note enregistrée avec son identifiant généré
     */
    public Note save(Note n) {
        var now = Instant.now();
        n.setCreatedAt(now);
        n.setUpdatedAt(now);
        n.setId(null);
        return repo.save(n);
    }

    /**
     * Met à jour une note existante.
     *
     * @param n note contenant les données mises à jour
     * @return la note mise à jour
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
     * @param id identifiant de la note à supprimer
     */
    public void delete(String id) {
        repo.deleteById(id);
    }
}
