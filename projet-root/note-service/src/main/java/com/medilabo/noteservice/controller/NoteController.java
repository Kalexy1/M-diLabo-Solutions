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

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteService service;

    public NoteController(NoteService service) {
        this.service = service;
    }

    // Récupérer toutes les notes d’un patient
    @GetMapping("/patient/{patientId}")
    public List<Note> findByPatient(@PathVariable Long patientId) {
        return service.findByPatientId(patientId);
    }

    // Détail d’une note
    @GetMapping("/{id}")
    public Note getOne(@PathVariable Long id) {
        return service.getById(id);
    }

    // Créer une note pour un patient
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/patient/{patientId}")
    public Note create(@PathVariable Long patientId, @RequestBody Note payload) {
        payload.setId(null); // ensure create
        payload.setPatientId(patientId);
        return service.save(payload);
    }

    // Mettre à jour une note
    @PutMapping("/{id}")
    public Note update(@PathVariable Long id, @RequestBody Note payload) {
        payload.setId(id);
        return service.update(payload);
    }

    // Supprimer une note
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
