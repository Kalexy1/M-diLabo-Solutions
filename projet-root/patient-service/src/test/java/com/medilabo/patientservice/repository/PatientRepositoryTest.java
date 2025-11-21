package com.medilabo.patientservice.repository;

import com.medilabo.patientservice.model.Patient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class PatientRepositoryTest {

    @Autowired
    private PatientRepository repository;

    @Test
    void shouldSaveAndFindById() {
        Patient p = new Patient("John", "Doe", LocalDate.of(1990, 1, 1), "M", "123 rue test", "0123456789");
        Patient saved = repository.save(p);

        assertThat(repository.findById(saved.getId())).isPresent();
        assertThat(repository.findById(saved.getId()).get().getLastName()).isEqualTo("Doe");
    }

    @Test
    void shouldFindAllPatients() {
        repository.save(new Patient("Alice", "Test", LocalDate.of(1980, 1, 1), "F", null, null));
        repository.save(new Patient("Bob", "Test", LocalDate.of(1970, 1, 1), "M", null, null));

        List<Patient> list = repository.findAll();
        assertThat(list).hasSizeGreaterThanOrEqualTo(2);
    }
}
