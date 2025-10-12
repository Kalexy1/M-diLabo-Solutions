package com.medilabo.patientservice.service;

import com.medilabo.patientservice.model.Patient;
import com.medilabo.patientservice.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PatientService {

    private final PatientRepository repo;

    public PatientService(PatientRepository repo) {
        this.repo = repo;
    }

    public List<Patient> findAll() {
        return repo.findAll();
    }

    public Patient getById(Long id) {
        return repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Patient introuvable: " + id));
    }

    public List<Patient> searchByLastName(String lastNamePart) {
        return repo.findByLastNameContainingIgnoreCase(lastNamePart == null ? "" : lastNamePart.trim());
    }

    @Transactional
    public Patient create(Patient p) {
        p.setId(null);
        return repo.save(p);
    }

    @Transactional
    public Patient update(Long id, Patient payload) {
        Patient existing = getById(id);
        existing.setFirstName(payload.getFirstName());
        existing.setLastName(payload.getLastName());
        existing.setBirthDate(payload.getBirthDate());
        existing.setGender(payload.getGender());
        existing.setAddress(payload.getAddress());
        existing.setPhone(payload.getPhone());
        return repo.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        repo.deleteById(id);
    }
}
