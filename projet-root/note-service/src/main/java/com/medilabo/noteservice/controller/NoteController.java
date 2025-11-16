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
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Contrôleur REST du microservice NoteService.
 * <p>
 * Gère les opérations CRUD sur les notes médicales associées aux patients.
 * Tous les endpoints sont sécurisés et accessibles uniquement aux utilisateurs
 * ayant le rôle {@code PRATICIEN}.
 * </p>
 */
@RestController
@RequestMapping("/api/notes")
public class NoteController {

    /**
     * Service de gestion des notes médicales.
     */
    private final NoteService service;

    /**
     * Constructeur du contrôleur de notes.
     *
     * @param service le service métier utilisé pour gérer les notes
     */
    public NoteController(NoteService service) {
        this.service = service;
    }

    /**
     * Récupère toutes les notes associées à un patient donné.
     *
     * @param patientId l’identifiant du patient
     * @return la liste des notes liées à ce patient
     */
    @PreAuthorize("hasRole('PRATICIEN')")
    @GetMapping("/patient/{patientId}")
    public List<Note> findByPatient(@PathVariable Long patientId) {
        return service.findByPatientId(patientId);
    }

    /**
     * Récupère une note spécifique à partir de son identifiant.
     *
     * @param id l’identifiant de la note
     * @return la note correspondante
     */
    @GetMapping("/{id}")
    public Note getOne(@PathVariable Long id) {
        return service.getById(id);
    }

    /**
     * Crée une nouvelle note pour un patient donné.
     *
     * @param patientId l’identifiant du patient concerné
     * @param payload   la note à créer
     * @return la note nouvellement créée
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
     * @param id      l’identifiant de la note à mettre à jour
     * @param payload les nouvelles données de la note
     * @return la note mise à jour
     */
    @PutMapping("/{id}")
    public Note update(@PathVariable Long id, @RequestBody Note payload) {
        payload.setId(id);
        return service.update(payload);
    }

    /**
     * Supprime une note à partir de son identifiant.
     *
     * @param id l’identifiant de la note à supprimer
     */
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
