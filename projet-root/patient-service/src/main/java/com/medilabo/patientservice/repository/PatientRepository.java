package com.medilabo.patientservice.repository;

import com.medilabo.patientservice.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Référentiel JPA pour l’entité {@link Patient}.
 * <p>
 * Fournit les opérations CRUD de base ainsi qu’une méthode personnalisée
 * permettant de rechercher des patients par une partie de leur nom de famille,
 * sans tenir compte de la casse.
 * </p>
 */
public interface PatientRepository extends JpaRepository<Patient, Long> {

    /**
     * Recherche les patients dont le nom de famille contient une sous-chaîne donnée,
     * sans distinction de casse.
     *
     * @param lastNamePart une partie du nom de famille à rechercher
     * @return la liste des patients correspondants
     */
    List<Patient> findByLastNameContainingIgnoreCase(String lastNamePart);
}
