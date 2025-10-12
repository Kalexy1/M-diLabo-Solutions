package com.medilabo.patientui.service;

import com.medilabo.patientui.model.Patient;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;

@Service
public class PatientService {

    private final WebClient patientApiClient;

    public PatientService(WebClient patientApiClient) {
        this.patientApiClient = patientApiClient;
    }

    public List<Patient> findAll(String jwt) {
        var spec = patientApiClient.get().uri("")
                .header(HttpHeaders.AUTHORIZATION, bearer(jwt));
        Patient[] arr = spec.retrieve().bodyToMono(Patient[].class).block();
        return arr == null ? List.of() : Arrays.asList(arr);
    }

    public Patient getOne(Long id, String jwt) {
        return patientApiClient.get().uri("/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, bearer(jwt))
                .retrieve().bodyToMono(Patient.class).block();
    }

    public Patient create(Patient payload, String jwt) {
        return patientApiClient.post().uri("")
                .header(HttpHeaders.AUTHORIZATION, bearer(jwt))
                .bodyValue(payload)
                .retrieve().bodyToMono(Patient.class).block();
    }

    public Patient update(Long id, Patient payload, String jwt) {
        return patientApiClient.put().uri("/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, bearer(jwt))
                .bodyValue(payload)
                .retrieve().bodyToMono(Patient.class).block();
    }

    public void delete(Long id, String jwt) {
        patientApiClient.delete().uri("/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, bearer(jwt))
                .retrieve().toBodilessEntity().block();
    }

    private static String bearer(String jwt) {
        return "Bearer " + (jwt == null ? "" : jwt);
    }
}
