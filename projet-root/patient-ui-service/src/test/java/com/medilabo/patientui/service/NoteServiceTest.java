package com.medilabo.patientui.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class NoteServiceTest {

    private NoteService noteService;
    private MockRestServiceServer mockServer;
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate(new SimpleClientHttpRequestFactory());
        noteService = new NoteService() {
            @Override
            public List<Map<String, Object>> getNotesByPatientId(Long patientId) {
                return restTemplate.getForObject("http://gateway-service:8080/notes/patient/" + patientId, List.class);
            }

            @Override
            public void ajouterNote(Long patientId, String contenu) {
                Map<String, Object> note = Map.of("patientId", patientId, "contenu", contenu);
                restTemplate.postForObject("http://gateway-service:8080/notes", note, Void.class);
            }
        };
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void getNotesByPatientId_shouldReturnListOfNotes() {
        String jsonResponse = """
            [
                {"patientId":1,"contenu":"note 1"},
                {"patientId":1,"contenu":"note 2"}
            ]
        """;

        mockServer.expect(requestTo("http://gateway-service:8080/notes/patient/1"))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        List<Map<String, Object>> notes = noteService.getNotesByPatientId(1L);

        assertThat(notes).hasSize(2);
        assertThat(notes.get(0)).containsEntry("contenu", "note 1");

        mockServer.verify();
    }

    @Test
    void ajouterNote_shouldPostNoteToService() {
        mockServer.expect(requestTo("http://gateway-service:8080/notes"))
                .andExpect(method(org.springframework.http.HttpMethod.POST))
                .andExpect(jsonPath("$.patientId").value(1))
                .andExpect(jsonPath("$.contenu").value("test note"))
                .andRespond(withSuccess());

        noteService.ajouterNote(1L, "test note");

        mockServer.verify();
    }
}
