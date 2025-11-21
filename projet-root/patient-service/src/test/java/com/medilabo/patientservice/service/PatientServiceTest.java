package com.medilabo.patientservice.service;

import com.medilabo.patientservice.model.Patient;
import com.medilabo.patientservice.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PatientServiceTest {

    private PatientService service;
    private PatientRepository repo;

    private Patient existing;

    @BeforeEach
    void setup() {
        repo = mock(PatientRepository.class);
        service = new PatientService(repo);

        existing = new Patient();
        existing.setId(1L);
        existing.setFirstName("Marie");
        existing.setLastName("Curie");
        existing.setBirthDate(LocalDate.of(1867, 11, 7));
        existing.setGender("F");
        existing.setAddress(null);
        existing.setPhone(null);
    }

    @Test
    void findAll_returnsAllPatients() {
        when(repo.findAll()).thenReturn(List.of(existing));

        List<Patient> patients = service.findAll();

        assertThat(patients).hasSize(1);
        verify(repo).findAll();
    }

    @Test
    void getById_returnsPatient_whenExists() {
        when(repo.findById(1L)).thenReturn(Optional.of(existing));

        Patient p = service.getById(1L);

        assertThat(p).isEqualTo(existing);
        verify(repo).findById(1L);
    }

    @Test
    void getById_throws_whenNotFound() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("99");

        verify(repo).findById(99L);
    }

    @Test
    void create_savesAndReturnsPatient() {
        when(repo.save(any(Patient.class))).thenAnswer(inv -> inv.getArgument(0));

        Patient payload = new Patient();
        payload.setFirstName("Marie");
        payload.setLastName("Curie");
        payload.setBirthDate(LocalDate.of(1867, 11, 7));
        payload.setGender("F");
        payload.setAddress("Paris");
        payload.setPhone("0102030405");

        Patient saved = service.create(payload);

        assertThat(saved.getLastName()).isEqualTo("Curie");
        verify(repo).save(payload);
    }

    @Test
    void update_updatesFields_whenExists() {
        when(repo.findById(1L)).thenReturn(Optional.of(existing));
        when(repo.save(any(Patient.class))).thenAnswer(inv -> inv.getArgument(0));

        Patient payload = new Patient();
        payload.setFirstName("Marie-Sklodowska");
        payload.setLastName("Curie");
        payload.setBirthDate(LocalDate.of(1867, 11, 7));
        payload.setGender("F");
        payload.setAddress("Paris");
        payload.setPhone("0102030405");

        Patient updated = service.update(1L, payload);

        assertThat(updated.getFirstName()).isEqualTo("Marie-Sklodowska");
        assertThat(updated.getAddress()).isEqualTo("Paris");
        assertThat(updated.getPhone()).isEqualTo("0102030405");

        verify(repo).findById(1L);
        verify(repo).save(any(Patient.class));
    }

    @Test
    void update_throws_whenNotFound() {
        when(repo.findById(42L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(42L, existing))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("42");

        verify(repo).findById(42L);
        verify(repo, never()).save(any());
    }

    @Test
    void delete_callsRepositoryDelete() {
        service.delete(1L);
        verify(repo).deleteById(1L);
    }
}
