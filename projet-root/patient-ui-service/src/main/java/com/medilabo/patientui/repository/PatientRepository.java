package com.medilabo.patientui.repository;

import com.medilabo.patientui.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Interface de repository pour l'entité {@link Patient} dans le module patient-ui.
 * <p>
 * Étend {@link JpaRepository} pour fournir les opérations CRUD de base
 * ainsi que la pagination et le tri si nécessaire.
 * </p>
 */
public interface PatientRepository extends JpaRepository<Patient, Long> {
}
