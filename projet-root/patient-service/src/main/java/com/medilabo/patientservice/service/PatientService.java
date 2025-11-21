package com.medilabo.patientservice.service;

import com.medilabo.patientservice.model.Patient;
import com.medilabo.patientservice.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service métier gérant la gestion des patients.
 *
 * <p>Ce service centralise les opérations de création, consultation,
 * modification et suppression des entités {@link Patient}. Il s'appuie sur
 * le {@link PatientRepository} pour interagir avec la base de données.</p>
 */
@Service
@Transactional(readOnly = true)
public class PatientService {

    /**
     * Repository d'accès aux données des patients.
     */
    private final PatientRepository repo;

    /**
     * Construit le service de gestion des patients.
     *
     * @param repo repository JPA pour les entités {@link Patient}
     */
    public PatientService(PatientRepository repo) {
        this.repo = repo;
    }

    /**
     * Récupère la liste de tous les patients.
     *
     * @return liste complète des patients enregistrés
     */
    public List<Patient> findAll() {
        return repo.findAll();
    }

    /**
     * Récupère un patient via son identifiant.
     *
     * @param id identifiant du patient
     * @return patient correspondant
     * @throws IllegalArgumentException si aucun patient n’est trouvé
     */
    public Patient getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Patient introuvable: " + id));
    }

    /**
     * Recherche les patients dont le nom de famille contient une sous-chaîne donnée,
     * sans tenir compte de la casse.
     *
     * @param lastNamePart fragment du nom de famille à rechercher
     * @return liste des patients correspondants
     */
    public List<Patient> searchByLastName(String lastNamePart) {
        return repo.findByLastNameContainingIgnoreCase(
                lastNamePart == null ? "" : lastNamePart.trim()
        );
    }

    /**
     * Crée un nouveau patient.
     *
     * <p>L'identifiant est remis à {@code null} afin de forcer la création
     * d'une nouvelle entrée en base.</p>
     *
     * @param p patient à enregistrer
     * @return patient créé et sauvegardé
     */
    @Transactional
    public Patient create(Patient p) {
        p.setId(null);
        return repo.save(p);
    }

    /**
     * Met à jour un patient existant avec les nouvelles informations fournies.
     *
     * @param id identifiant du patient à mettre à jour
     * @param payload nouvelles données du patient
     * @return patient mis à jour
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
     * Supprime un patient via son identifiant.
     *
     * @param id identifiant du patient à supprimer
     */
    @Transactional
    public void delete(Long id) {
        repo.deleteById(id);
    }
}
