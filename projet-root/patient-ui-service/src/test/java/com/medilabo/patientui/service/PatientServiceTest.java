package com.medilabo.patientui.service;

import com.medilabo.patientui.model.Patient;
import com.medilabo.patientui.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PatientServiceTest {

    private PatientRepository patientRepository;
    private PatientService patientService;

    private Patient patient;

    @BeforeEach
    void setUp() {
        patientRepository = mock(PatientRepository.class);
        patientService = new PatientService(patientRepository);

        patient = new Patient();
        patient.setId(1L);
        patient.setNom("Doe");
        patient.setPrenom("John");
        patient.setGenre("M");
        patient.setDateNaissance(LocalDate.of(1990, 1, 1));
    }

    @Test
    void findAll_shouldReturnListOfPatients() {
        when(patientRepository.findAll()).thenReturn(List.of(patient));

        List<Patient> result = patientService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNom()).isEqualTo("Doe");
        verify(patientRepository).findAll();
    }

    @Test
    void findById_shouldReturnPatient() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        Optional<Patient> result = patientService.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getPrenom()).isEqualTo("John");
        verify(patientRepository).findById(1L);
    }

    @Test
    void save_shouldSaveAndReturnPatient() {
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        Patient result = patientService.save(patient);

        assertThat(result.getId()).isEqualTo(1L);
        verify(patientRepository).save(patient);
    }

    @Test
    void deleteById_shouldCallRepository() {
        patientService.deleteById(1L);

        verify(patientRepository).deleteById(1L);
    }
}
