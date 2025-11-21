package com.medilabo.noteservice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.medilabo.noteservice.model.Note;
import com.medilabo.noteservice.service.NoteService;

/**
 * Contrôleur REST du microservice NoteService.
 *
 * <p>Gère les opérations CRUD sur les notes médicales associées aux patients.
 * Toutes les routes exposées sont situées sous {@code /api/notes}.</p>
 */
@RestController
@RequestMapping("/api/notes")
public class NoteController {

    /**
     * Service métier responsable de la gestion des notes médicales.
     */
    private final NoteService service;

    /**
     * Construit un contrôleur permettant d'exposer les opérations liées aux notes.
     *
     * @param service service métier de gestion des notes
     */
    public NoteController(NoteService service) {
        this.service = service;
    }

    /**
     * Récupère toutes les notes associées à un patient.
     *
     * @param patientId identifiant du patient
     * @return liste des notes liées au patient
     */
    @GetMapping("/patient/{patientId}")
    public List<Note> findByPatient(@PathVariable Long patientId) {
        return service.findByPatientId(patientId);
    }

    /**
     * Récupère une note spécifique à partir de son identifiant.
     *
     * @param id identifiant de la note
     * @return note correspondante
     */
    @GetMapping("/{id}")
    public Note getOne(@PathVariable String id) {
        return service.getById(id);
    }

    /**
     * Crée une nouvelle note pour un patient donné.
     *
     * <p>L'identifiant de la note est remis à {@code null} afin de laisser la base
     * de données en générer un nouveau.</p>
     *
     * @param patientId identifiant du patient concerné
     * @param payload   contenu de la nouvelle note
     * @return la note créée
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/patient/{patientId}")
    public Note create(@PathVariable Long patientId, @RequestBody Note payload) {
        payload.setId(null);
        payload.setPatientId(patientId);
        return service.save(payload);
    }

    /**
     * Met à jour une note existante.
     *
     * @param id identifiant de la note à mettre à jour
     * @param payload données de la note modifiée
     * @return la note mise à jour
     */
    @PutMapping("/{id}")
    public Note update(@PathVariable String id, @RequestBody Note payload) {
        payload.setId(id);
        return service.update(payload);
    }

    /**
     * Supprime une note à partir de son identifiant.
     *
     * @param id identifiant de la note à supprimer
     */
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}
