package com.medilabo.patientservice.service;

import com.medilabo.patientservice.model.Patient;
import com.medilabo.patientservice.repository.PatientRepository;
import com.medilabo.patientservice.service.PatientService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PatientServiceTest {

    @InjectMocks
    private PatientService patientService;

    @Mock
    private PatientRepository patientRepository;

    private Patient existing;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        existing = new Patient("Marie", "Curie", LocalDate.of(1867, 11, 7), "F", null, null);
        existing.setId(1L);
    }

    @Test
    void shouldReturnAllPatients() {
        when(patientRepository.findAll()).thenReturn(List.of(existing));
        List<Patient> patients = patientService.getAllPatients();
        assertThat(patients).hasSize(1);
        verify(patientRepository).findAll();
    }

    @Test
    void shouldFindPatientById() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(existing));
        Optional<Patient> found = patientService.getPatientById(1L);
        assertThat(found).isPresent().contains(existing);
        verify(patientRepository).findById(1L);
    }

    @Test
    void shouldReturnEmptyWhenNotFoundById() {
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());
        Optional<Patient> found = patientService.getPatientById(99L);
        assertThat(found).isEmpty();
        verify(patientRepository).findById(99L);
    }

    @Test
    void shouldSavePatient() {
        when(patientRepository.save(any(Patient.class))).thenAnswer(inv -> inv.getArgument(0));
        Patient saved = patientService.savePatient(existing);
        assertThat(saved.getNom()).isEqualTo("Curie");
        verify(patientRepository).save(existing);
    }

    @Test
    void shouldUpdatePatient_whenExists() {
        Patient toUpdate = new Patient("Marie-Sklodowska", "Curie", LocalDate.of(1867, 11, 7), "F", "Paris", "0102030405");
        toUpdate.setId(1L);
        when(patientRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(patientRepository.save(any(Patient.class))).thenAnswer(inv -> inv.getArgument(0));

        Optional<Patient> updated = patientService.updatePatient(1L, toUpdate);

        assertThat(updated).isPresent();
        Patient p = updated.get();
        assertThat(p.getPrenom()).isEqualTo("Marie-Sklodowska");
        assertThat(p.getAdresse()).isEqualTo("Paris");
        assertThat(p.getTelephone()).isEqualTo("0102030405");
        verify(patientRepository).findById(1L);
        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    void shouldNotUpdate_whenNotFound() {
        when(patientRepository.findById(42L)).thenReturn(Optional.empty());
        Optional<Patient> updated = patientService.updatePatient(42L, existing);
        assertThat(updated).isEmpty();
        verify(patientRepository).findById(42L);
        verify(patientRepository, never()).save(any());
    }

    @Test
    void shouldDeletePatient_whenExists() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(existing));
        boolean result = patientService.deletePatient(1L);
        assertThat(result).isTrue();
        verify(patientRepository).findById(1L);
        verify(patientRepository).deleteById(1L);
    }

    @Test
    void shouldNotDelete_whenNotFound() {
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());
        boolean result = patientService.deletePatient(99L);
        assertThat(result).isFalse();
        verify(patientRepository).findById(99L);
        verify(patientRepository, never()).deleteById(anyLong());
    }
}
