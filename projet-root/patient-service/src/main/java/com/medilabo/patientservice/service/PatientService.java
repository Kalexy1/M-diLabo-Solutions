package com.medilabo.patientservice.service;

import com.medilabo.patientservice.model.Patient;
import com.medilabo.patientservice.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service métier pour la gestion des {@link Patient}.
 * <p>
 * Ce service centralise l'accès au {@link PatientRepository} afin d'éviter son utilisation
 * directe par les contrôleurs et d'y regrouper la logique métier liée aux patients.
 * </p>
 */
@Service
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    /**
     * Récupère la liste de tous les patients.
     *
     * @return liste de patients
     */
    public List<Patient> getAll() {
        return patientRepository.findAll();
    }

    /**
     * Recherche un patient par son identifiant.
     *
     * @param id identifiant du patient
     * @return patient correspondant s'il existe
     */
    public Optional<Patient> getById(Long id) {
        return patientRepository.findById(id);
    }

    /**
     * Crée un nouveau patient.
     *
     * @param patient patient à sauvegarder
     * @return patient sauvegardé
     */
    public Patient create(Patient patient) {
        return patientRepository.save(patient);
    }

    /**
     * Met à jour les informations d'un patient existant.
     *
     * @param id identifiant du patient
     * @param updatedPatient informations à mettre à jour
     * @return patient mis à jour s'il existe
     */
    public Optional<Patient> update(Long id, Patient updatedPatient) {
        return patientRepository.findById(id).map(patient -> {
            patient.setPrenom(updatedPatient.getPrenom());
            patient.setNom(updatedPatient.getNom());
            patient.setDateNaissance(updatedPatient.getDateNaissance());
            patient.setGenre(updatedPatient.getGenre());
            patient.setAdresse(updatedPatient.getAdresse());
            patient.setTelephone(updatedPatient.getTelephone());
            return patientRepository.save(patient);
        });
    }

    /**
     * Supprime un patient.
     *
     * @param id identifiant du patient
     * @return true si le patient a été supprimé
     */
    public boolean delete(Long id) {
        if (patientRepository.existsById(id)) {
            patientRepository.deleteById(id);
            return true;
        }
        return false;
    }
}