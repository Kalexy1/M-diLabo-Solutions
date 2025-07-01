package com.medilabo.noteservice.controller;

import com.medilabo.noteservice.model.Note;
import com.medilabo.noteservice.repository.NoteRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des notes médicales des patients.
 * <p>
 * Ce contrôleur permet de récupérer les notes associées à un patient
 * et d'en ajouter de nouvelles.
 * </p>
 * <p>
 * Toutes les routes sont accessibles sous le préfixe <code>/notes</code>.
 * </p>
 * 
 * @author [Ton Nom]
 * @version 1.0
 */
@RestController
@RequestMapping("/notes")
public class NoteController {

    private final NoteRepository repository;

    /**
     * Constructeur injectant le {@link NoteRepository}.
     *
     * @param repository le repository de gestion des notes
     */
    public NoteController(NoteRepository repository) {
        this.repository = repository;
    }

    /**
     * Récupère toutes les notes liées à un patient donné.
     *
     * @param patientId l'identifiant du patient
     * @return une liste des notes du patient
     */
    @GetMapping("/patient/{patientId}")
    public List<Note> getNotesByPatient(@PathVariable Integer patientId) {
        return repository.findByPatientId(patientId);
    }

    /**
     * Enregistre une nouvelle note médicale pour un patient.
     *
     * @param note la note à ajouter
     * @return la note enregistrée
     */
    @PostMapping
    public Note addNote(@RequestBody Note note) {
        return repository.save(note);
    }
}
