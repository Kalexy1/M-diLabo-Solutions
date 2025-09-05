package com.medilabo.patientui.repository;

import com.medilabo.patientui.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Application: com.medilabo.patientui.repository
 * <p>
 * Interface <strong>PatientRepository</strong>.
 * <br/>
 * Rôle : Assure la persistance des entités {@link Patient} dans la base de données
 * du microservice <em>patient-ui-service</em>.
 * </p>
 * <p>
 * Elle hérite de {@link JpaRepository}, ce qui fournit automatiquement :
 * <ul>
 *   <li>Les opérations CRUD de base ({@code save}, {@code findById}, {@code findAll}, {@code deleteById}).</li>
 *   <li>La pagination et le tri via {@code Pageable} et {@code Sort}.</li>
 *   <li>La possibilité de définir des requêtes personnalisées avec Spring Data JPA si nécessaire.</li>
 * </ul>
 * </p>
 */
public interface PatientRepository extends JpaRepository<Patient, Long> {
}
