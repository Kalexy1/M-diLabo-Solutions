package com.medilabo.patientui.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.medilabo.patientui.model.Patient;

/**
 * Service chargé de la communication avec le microservice patient-service.
 * <p>
 * Fournit des opérations CRUD pour les patients via des appels REST.
 * </p>
 */
@Service
public class PatientService {

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * URL de base exposée par le gateway pour le microservice patient-service.
     */
    private static final String PATIENT_SERVICE_URL = "http://gateway-service:8080/patients";

    /**
     * Récupère la liste de tous les patients depuis le microservice patient-service.
     *
     * @return une liste de patients
     */
    public List<Patient> getAllPatients() {
        Patient[] patients = restTemplate.getForObject(PATIENT_SERVICE_URL, Patient[].class);
        return patients != null ? Arrays.asList(patients) : List.of();
    }

    /**
     * Récupère un patient par son identifiant.
     *
     * @param id identifiant du patient
     * @return un {@link Optional} contenant le patient s'il existe
     */
    public Optional<Patient> getPatientById(Long id) {
        return Optional.ofNullable(restTemplate.getForObject(PATIENT_SERVICE_URL + "/" + id, Patient.class));
    }

    /**
     * Crée un nouveau patient.
     *
     * @param patient patient à créer
     * @return le patient créé
     */
    public Patient createPatient(Patient patient) {
        return restTemplate.postForObject(PATIENT_SERVICE_URL, patient, Patient.class);
    }

    /**
     * Met à jour un patient existant.
     *
     * @param id identifiant du patient
     * @param patient données du patient mises à jour
     */
    public void updatePatient(Long id, Patient patient) {
        restTemplate.put(PATIENT_SERVICE_URL + "/" + id, patient);
    }

    /**
     * Supprime un patient par son identifiant.
     *
     * @param id identifiant du patient
     */
    public void deletePatient(Long id) {
        restTemplate.delete(PATIENT_SERVICE_URL + "/" + id);
    }
}