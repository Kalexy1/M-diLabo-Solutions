package com.medilabo.patientui.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class NoteServiceTest {

    private RestTemplate restTemplate;
    private NoteService noteService;

    @BeforeEach
    void setUp() {
        restTemplate = mock(RestTemplate.class);
        noteService = new NoteService(restTemplate);
    }

    @Test
    void getNotesByPatientId_shouldReturnListOfNotes() {
        List<Map<String, Object>> mockNotes = List.of(
            Map.of("contenu", "Fumeur"),
            Map.of("contenu", "Anticorps")
        );

        Long patientId = 1L;
        String expectedUrl = "http://gateway-service:8080/notes/patient/" + patientId;

        when(restTemplate.getForObject(expectedUrl, List.class)).thenReturn(mockNotes);

        List<Map<String, Object>> result = noteService.getNotesByPatientId(patientId);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).get("contenu")).isEqualTo("Fumeur");
        assertThat(result.get(1).get("contenu")).isEqualTo("Anticorps");
    }

    @Test
    void ajouterNote_shouldCallPostForObject() {
        Long patientId = 2L;
        String contenu = "Cholestérol élevé";
        String expectedUrl = "http://gateway-service:8080/notes";

        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);

        when(restTemplate.postForObject(eq(expectedUrl), any(), eq(Void.class))).thenReturn(null);

        noteService.ajouterNote(patientId, contenu);

        verify(restTemplate, times(1)).postForObject(eq(expectedUrl), captor.capture(), eq(Void.class));

        Map<String, Object> sentData = captor.getValue();
        assertThat(sentData.get("patientId")).isEqualTo(patientId);
        assertThat(sentData.get("contenu")).isEqualTo(contenu);
    }
}
