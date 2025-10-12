package com.medilabo.noteservice.service;

import com.medilabo.noteservice.model.Note;
import com.medilabo.noteservice.repository.NoteRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class NoteService {
    private final NoteRepository repo;

    public NoteService(NoteRepository repo) {
        this.repo = repo;
    }

    public List<Note> findByPatientId(Long patientId) {
        return repo.findByPatientId(patientId);
    }

    public Note getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Note introuvable: " + id));
    }

    public Note save(Note n) {
        var now = Instant.now();
        n.setCreatedAt(now);
        n.setUpdatedAt(now);
        return repo.save(n);
    }

    public Note update(Note n) {
        var existing = getById(n.getId());
        existing.setContent(n.getContent());
        existing.setUpdatedAt(Instant.now());
        return repo.save(existing);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
