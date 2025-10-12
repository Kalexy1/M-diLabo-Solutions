package com.medilabo.patientservice.service;

import com.medilabo.patientservice.model.Patient;
import com.medilabo.patientservice.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service gérant la logique métier liée à la gestion des patients.
 * <p>
 * Cette classe assure la création, la consultation, la mise à jour et la suppression
 * des entités {@link Patient} en s’appuyant sur le {@link PatientRepository}.
 * </p>
 */
@Service
@Transactional(readOnly = true)
public class PatientService {

    /**
     * Référentiel d’accès aux données des patients.
     */
    private final PatientRepository repo;

    /**
     * Constructeur injectant le repository de gestion des patients.
     *
     * @param repo le repository {@link PatientRepository} à utiliser
     */
    public PatientService(PatientRepository repo) {
        this.repo = repo;
    }

    /**
     * Récupère la liste de tous les patients.
     *
     * @return la liste complète des patients enregistrés
     */
    public List<Patient> findAll() {
        return repo.findAll();
    }

    /**
     * Recherche un patient à partir de son identifiant.
     *
     * @param id l’identifiant du patient
     * @return le patient correspondant
     * @throws IllegalArgumentException si aucun patient n’est trouvé
     */
    public Patient getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Patient introuvable: " + id));
    }

    /**
     * Recherche des patients dont le nom de famille contient une sous-chaîne donnée.
     *
     * @param lastNamePart une partie du nom de famille (non sensible à la casse)
     * @return la liste des patients correspondants
     */
    public List<Patient> searchByLastName(String lastNamePart) {
        return repo.findByLastNameContainingIgnoreCase(
                lastNamePart == null ? "" : lastNamePart.trim()
        );
    }

    /**
     * Crée un nouveau patient.
     * <p>
     * L’identifiant est forcé à {@code null} pour garantir une nouvelle insertion.
     * </p>
     *
     * @param p le patient à créer
     * @return le patient créé et enregistré
     */
    @Transactional
    public Patient create(Patient p) {
        p.setId(null);
        return repo.save(p);
    }

    /**
     * Met à jour un patient existant avec les nouvelles informations fournies.
     *
     * @param id      l’identifiant du patient à mettre à jour
     * @param payload les nouvelles données du patient
     * @return le patient mis à jour
     * @throws IllegalArgumentException si le patient n’existe pas
     */
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

    /**
     * Supprime un patient à partir de son identifiant.
     *
     * @param id l’identifiant du patient à supprimer
     */
    @Transactional
    public void delete(Long id) {
        repo.deleteById(id);
    }
}
