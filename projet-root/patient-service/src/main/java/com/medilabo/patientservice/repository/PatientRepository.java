package com.medilabo.patientservice.repository;

import com.medilabo.patientservice.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Application: com.medilabo.patientservice.repository
 * <p>
 * Interface <strong>PatientRepository</strong>.
 * <br/>
 * Rôle : Accès aux données des patients via Spring Data JPA.
 * </p>
 */
public interface PatientRepository extends JpaRepository<Patient, Long> {

    /**
     * Recherche des patients par nom (insensible à la casse selon provider).
     *
     * @param nom nom de famille.
     * @return liste de patients correspondant.
     */
    List<Patient> findByNom(String nom);
}
