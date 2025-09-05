package com.medilabo.noteservice.controller;

import com.medilabo.noteservice.model.Note;
import com.medilabo.noteservice.service.NoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Application: com.medilabo.noteservice.controller
 * <p>
 * Classe <strong>NoteController</strong>.
 * <br/>
 * Rôle : Contrôleur REST pour la gestion des notes médicales des patients.
 * Accessible uniquement aux praticiens.
 * </p>
 */
@RestController
@RequestMapping("/notes")
public class NoteController {

    private final NoteService noteService;

    /**
     * Constructeur avec injection du service métier.
     *
     * @param noteService service de gestion des notes
     */
    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    /**
     * Récupère toutes les notes associées à un patient.
     *
     * @param patientId identifiant du patient
     * @return liste des notes ou 204 No Content si aucune note
     */
    @PreAuthorize("hasRole('PRATICIEN')")
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Note>> getNotesByPatient(@PathVariable Integer patientId) {
        List<Note> notes = noteService.getNotesByPatientId(patientId);
        if (notes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(notes);
    }

    /**
     * Ajoute une nouvelle note pour un patient.
     *
     * @param note objet Note à ajouter
     * @return la note créée avec code HTTP 201 Created
     */
    @PreAuthorize("hasRole('PRATICIEN')")
    @PostMapping
    public ResponseEntity<Note> addNote(@RequestBody Note note) {
        Note savedNote = noteService.addNote(note);
        return new ResponseEntity<>(savedNote, HttpStatus.CREATED);
    }
}
