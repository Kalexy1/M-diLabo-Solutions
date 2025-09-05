package com.medilabo.patientservice.service;

import com.medilabo.patientservice.model.Patient;
import com.medilabo.patientservice.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Application: com.medilabo.patientservice.service
 * <p>
 * Classe <strong>PatientService</strong>.
 * <br/>
 * Rôle : Logique métier pour la gestion des patients.
 * </p>
 */
@Service
public class PatientService {

    private final PatientRepository patientRepository;

    /**
     * Constructeur d'injection.
     *
     * @param patientRepository repository des patients.
     */
    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    /**
     * Retourne la liste de tous les patients.
     *
     * @return liste de patients.
     */
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    /**
     * Retourne un patient par identifiant.
     *
     * @param id identifiant du patient.
     * @return {@link Optional} contenant le patient s'il existe.
     */
    public Optional<Patient> getPatientById(Long id) {
        return patientRepository.findById(id);
    }

    /**
     * Sauvegarde un patient (création ou mise à jour).
     *
     * @param patient patient à persister.
     * @return patient enregistré.
     */
    public Patient savePatient(Patient patient) {
        return patientRepository.save(patient);
    }

    /**
     * Met à jour un patient existant.
     *
     * @param id identifiant du patient.
     * @param toUpdate données à appliquer.
     * @return {@link Optional} du patient mis à jour, vide si introuvable.
     */
    public Optional<Patient> updatePatient(Long id, Patient toUpdate) {
        return patientRepository.findById(id).map(existing -> {
            existing.setNom(toUpdate.getNom());
            existing.setPrenom(toUpdate.getPrenom());
            existing.setDateNaissance(toUpdate.getDateNaissance());
            existing.setGenre(toUpdate.getGenre());
            existing.setAdresse(toUpdate.getAdresse());
            existing.setTelephone(toUpdate.getTelephone());
            return patientRepository.save(existing);
        });
    }

    /**
     * Supprime un patient s'il existe.
     *
     * @param id identifiant du patient.
     * @return {@code true} si supprimé, {@code false} sinon.
     */
    public boolean deletePatient(Long id) {
        return patientRepository.findById(id).map(p -> {
            patientRepository.deleteById(id);
            return true;
        }).orElse(false);
    }
}
