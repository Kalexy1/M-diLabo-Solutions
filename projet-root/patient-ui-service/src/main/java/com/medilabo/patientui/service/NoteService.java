package com.medilabo.patientui.service;

import com.medilabo.patientui.model.Note;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;

@Service
public class NoteService {
    private final WebClient noteApiClient;

    public NoteService(WebClient noteApiClient) {
        this.noteApiClient = noteApiClient;
    }

    public List<Note> findByPatient(Long patientId, String jwt) {
        Note[] arr = noteApiClient.get().uri("/patient/{pid}", patientId)
                .header(HttpHeaders.AUTHORIZATION, bearer(jwt))
                .retrieve().bodyToMono(Note[].class).block();
        return arr == null ? List.of() : Arrays.asList(arr);
    }

    public Note createForPatient(Long patientId, Note payload, String jwt) {
        return noteApiClient.post().uri("/patient/{pid}", patientId)
                .header(HttpHeaders.AUTHORIZATION, bearer(jwt))
                .bodyValue(payload)
                .retrieve().bodyToMono(Note.class).block();
    }

    private static String bearer(String jwt) {
        return "Bearer " + (jwt == null ? "" : jwt);
    }
}
