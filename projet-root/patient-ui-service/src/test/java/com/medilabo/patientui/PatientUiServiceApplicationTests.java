package com.medilabo.patientui;

import com.medilabo.patientui.controller.PatientController;
import com.medilabo.patientui.service.PatientService;
import com.medilabo.patientui.service.NoteService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

@WebMvcTest(PatientController.class)
@ActiveProfiles("test")
class PatientUiServiceApplicationTests {

    @MockBean PatientService patientService;
    @MockBean NoteService noteService;
    @MockBean RestTemplate restTemplate;

    @Test
    void contextLoads() { }
}
