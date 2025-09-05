package com.medilabo.noteservice.service;

import com.medilabo.noteservice.model.Note;
import com.medilabo.noteservice.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Application: com.medilabo.noteservice.service
 * <p>
 * Classe <strong>NoteService</strong>.
 * <br/>
 * Rôle : Service métier pour gérer les notes médicales des patients.
 * </p>
 */
@Service
public class NoteService {

    @Autowired
    private NoteRepository noteRepository;

    /**
     * Constructeur permettant l'injection du repository.
     *
     * @param noteRepository repository des notes
     */
    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    /**
     * Récupère toutes les notes associées à un patient donné.
     *
     * @param patientId l'identifiant du patient
     * @return la liste des notes du patient
     */
    public List<Note> getNotesByPatientId(Integer patientId) {
        return noteRepository.findByPatientId(patientId);
    }

    /**
     * Ajoute une nouvelle note pour un patient.
     *
     * @param note la note à ajouter
     * @return la note enregistrée
     */
    public Note addNote(Note note) {
        return noteRepository.save(note);
    }

    /**
     * Supprime une note par son identifiant.
     *
     * @param id l'identifiant de la note à supprimer
     */
    public void deleteNoteById(String id) {
        noteRepository.deleteById(id);
    }
}
