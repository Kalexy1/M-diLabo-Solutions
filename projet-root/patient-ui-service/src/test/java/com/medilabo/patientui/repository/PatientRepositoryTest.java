package com.medilabo.patientui.repository;

import com.medilabo.patientui.model.Patient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class PatientRepositoryTest {

    @Autowired
    private PatientRepository patientRepository;

    @Test
    void testSaveAndFindById() {
        // Given
        Patient patient = new Patient();
        patient.setNom("Dupont");
        patient.setPrenom("Jean");
        patient.setDateNaissance(LocalDate.of(1980, 1, 1));
        patient.setGenre("M");
        patient.setAdresse("1 rue des Lilas");
        patient.setTelephone("0601020304");

        // When
        Patient saved = patientRepository.save(patient);
        Patient found = patientRepository.findById(saved.getId()).orElse(null);

        // Then
        assertThat(found).isNotNull();
        assertThat(found.getNom()).isEqualTo("Dupont");
        assertThat(found.getPrenom()).isEqualTo("Jean");
    }

    @Test
    void testDelete() {
        Patient patient = new Patient();
        patient.setNom("Martin");
        patient.setPrenom("Marie");
        patient.setDateNaissance(LocalDate.of(1990, 5, 15));
        patient.setGenre("F");

        Patient saved = patientRepository.save(patient);
        Long id = saved.getId();

        patientRepository.deleteById(id);

        assertThat(patientRepository.findById(id)).isEmpty();
    }
}
