package com.medilabo.patientui.service;

import com.medilabo.patientui.model.Patient;
import com.medilabo.patientui.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Application: com.medilabo.patientui.service
 * <p>
 * Classe <strong>PatientService</strong>.
 * <br/>
 * Rôle : Assure la gestion métier des patients dans le microservice <em>patient-ui-service</em>.
 * </p>
 * <p>
 * Cette classe encapsule les opérations de persistence via {@link PatientRepository}
 * et est utilisée par les contrôleurs pour :
 * <ul>
 *   <li>Récupérer la liste complète des patients.</li>
 *   <li>Rechercher un patient par son identifiant.</li>
 *   <li>Enregistrer ou mettre à jour un patient.</li>
 *   <li>Supprimer un patient existant.</li>
 * </ul>
 * </p>
 */
@Service
public class PatientService {

    private final PatientRepository patientRepository;

    /**
     * Constructeur avec injection de dépendance.
     *
     * @param patientRepository repository utilisé pour la persistance des patients
     */
    @Autowired
    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    /**
     * Récupère tous les patients enregistrés en base.
     *
     * @return une liste de patients persistés
     */
    public List<Patient> findAll() {
        return patientRepository.findAll();
    }

    /**
     * Enregistre ou met à jour un patient.
     *
     * @param patient patient à persister
     * @return le patient sauvegardé (avec identifiant généré si nouveau)
     */
    public Patient save(Patient patient) {
        return patientRepository.save(patient);
    }

    /**
     * Recherche un patient par son identifiant.
     *
     * @param id identifiant unique du patient
     * @return un {@link Optional} contenant le patient s’il existe, vide sinon
     */
    public Optional<Patient> findById(Long id) {
        return patientRepository.findById(id);
    }

    /**
     * Supprime un patient par son identifiant.
     *
     * @param id identifiant du patient à supprimer
     */
    public void deleteById(Long id) {
        patientRepository.deleteById(id);
    }
}
