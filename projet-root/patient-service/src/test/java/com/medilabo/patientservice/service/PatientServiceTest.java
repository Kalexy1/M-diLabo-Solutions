package com.medilabo.patientservice.service;

import com.medilabo.patientservice.model.Patient;
import com.medilabo.patientservice.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository repository;

    @InjectMocks
    private PatientService service;

    @Test
    void getAll_returnsList() {
        when(repository.findAll()).thenReturn(List.of(new Patient()));

        List<Patient> result = service.getAll();

        assertThat(result).hasSize(1);
        verify(repository).findAll();
    }

    @Test
    void getById_found() {
        Patient patient = new Patient();
        when(repository.findById(1L)).thenReturn(Optional.of(patient));

        Optional<Patient> result = service.getById(1L);

        assertThat(result).contains(patient);
        verify(repository).findById(1L);
    }

    @Test
    void getById_notFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        Optional<Patient> result = service.getById(1L);

        assertThat(result).isEmpty();
        verify(repository).findById(1L);
    }

    @Test
    void create_savesPatient() {
        Patient patient = new Patient();
        when(repository.save(patient)).thenReturn(patient);

        Patient result = service.create(patient);

        assertThat(result).isEqualTo(patient);
        verify(repository).save(patient);
    }

    @Test
    void update_found() {
        Patient existing = new Patient();
        existing.setId(1L);
        existing.setPrenom("Alice");
        Patient update = new Patient();
        update.setPrenom("Bob");
        update.setNom("Durand");
        update.setGenre("M");
        update.setDateNaissance(LocalDate.of(1990,1,1));
        update.setAdresse("1 rue test");
        update.setTelephone("0102030405");

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Patient> result = service.update(1L, update);

        assertThat(result).isPresent();
        assertThat(result.get().getPrenom()).isEqualTo("Bob");
        verify(repository).save(existing);
    }

    @Test
    void update_notFound() {
        Patient update = new Patient();
        when(repository.findById(1L)).thenReturn(Optional.empty());

        Optional<Patient> result = service.update(1L, update);

        assertThat(result).isEmpty();
        verify(repository, never()).save(any());
    }

    @Test
    void delete_found() {
        when(repository.existsById(1L)).thenReturn(true);

        boolean result = service.delete(1L);

        assertThat(result).isTrue();
        verify(repository).deleteById(1L);
    }

    @Test
    void delete_notFound() {
        when(repository.existsById(1L)).thenReturn(false);

        boolean result = service.delete(1L);

        assertThat(result).isFalse();
        verify(repository, never()).deleteById(anyLong());
    }
}