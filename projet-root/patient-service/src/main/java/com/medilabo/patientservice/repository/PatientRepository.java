package com.medilabo.patientservice.repository;

import com.medilabo.patientservice.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository JPA pour la gestion des entités {@link Patient}.
 *
 * <p>Fournit les opérations CRUD standard et une méthode personnalisée
 * permettant de rechercher des patients via une sous-chaîne de leur nom
 * de famille, sans tenir compte de la casse.</p>
 */
public interface PatientRepository extends JpaRepository<Patient, Long> {

    /**
     * Recherche les patients dont le nom de famille contient la sous-chaîne
     * fournie, indépendamment de la casse.
     *
     * @param lastNamePart fragment du nom de famille à rechercher
     * @return liste des patients correspondants
     */
    List<Patient> findByLastNameContainingIgnoreCase(String lastNamePart);
}
