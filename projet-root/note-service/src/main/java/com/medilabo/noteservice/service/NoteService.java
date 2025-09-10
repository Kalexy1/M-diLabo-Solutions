package com.medilabo.noteservice.service;

import com.medilabo.noteservice.model.Note;
import com.medilabo.noteservice.repository.NoteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service métier pour la gestion des {@link Note}.
 * <p>
 * Ce service encapsule l'accès au {@link NoteRepository} afin d'éviter son utilisation directe
 * dans les contrôleurs et de centraliser la logique métier.
 * </p>
 */
@Service
public class NoteService {

    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    /**
     * Retourne l'ensemble des notes d'un patient.
     *
     * @param patientId identifiant du patient
     * @return liste des notes du patient
     */
    public List<Note> getNotesByPatient(Integer patientId) {
        return noteRepository.findByPatientId(patientId);
    }

    /**
     * Enregistre une nouvelle note médicale.
     *
     * @param note la note à sauvegarder
     * @return la note persistée
     */
    public Note addNote(Note note) {
        return noteRepository.save(note);
    }
    
    /**
     * Supprime toutes les notes.
     * Principalement utilisé pour préparer les jeux de données de test.
     */
    public void deleteAll() {
        noteRepository.deleteAll();
    }
}