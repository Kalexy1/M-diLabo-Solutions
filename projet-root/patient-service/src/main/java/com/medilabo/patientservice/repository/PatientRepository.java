package com.medilabo.patientservice.repository;

import com.medilabo.patientservice.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Interface de repository pour l'accès aux données des {@link Patient}.
 * <p>
 * Étend {@link JpaRepository} pour fournir les opérations CRUD de base ainsi que la pagination
 * et le tri sur l'entité {@link Patient}.
 * </p>
 */
public interface PatientRepository extends JpaRepository<Patient, Long> {
}
