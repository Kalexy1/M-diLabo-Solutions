package com.medilabo.patientui.controller;

import com.medilabo.patientui.model.Patient;
import com.medilabo.patientui.model.RiskAssessmentResponse;
import com.medilabo.patientui.service.NoteService;
import com.medilabo.patientui.service.PatientService;
import com.medilabo.patientui.service.RiskService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.jsonb.JsonbAutoConfiguration;
import org.springframework.boot.autoconfigure.mustache.MustacheAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = PatientController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class,
                UserDetailsServiceAutoConfiguration.class,
                OAuth2ClientAutoConfiguration.class,
                OAuth2ResourceServerAutoConfiguration.class,
                ThymeleafAutoConfiguration.class
        }
)
@AutoConfigureMockMvc(addFilters = false)
@ImportAutoConfiguration({
        HttpMessageConvertersAutoConfiguration.class,
        GsonAutoConfiguration.class,
        JsonbAutoConfiguration.class,
        FreeMarkerAutoConfiguration.class,
        MustacheAutoConfiguration.class
})
class PatientControllerTest {

    @Autowired MockMvc mvc;

    @MockBean PatientService patientService;
    @MockBean NoteService noteService;
    @MockBean RiskService riskService;

    @TestConfiguration
    static class NoOpViewResolverConfig {

        private static View noOpView() {
            return new View() {
                @Override public String getContentType() { return "text/html"; }
                @Override
                public void render(Map<String, ?> model,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {

                }
            };
        }

        /** Ne résout PAS redirect:/ ni forward:/ pour laisser Spring renvoyer 3xx. */
        static class HighPriorityNoOpResolver implements ViewResolver, Ordered {
            @Override
            public View resolveViewName(String viewName, Locale locale) {
                if (viewName == null) return null;
                if (viewName.startsWith("redirect:")) return null; // Laisse Spring gérer la redirection (3xx)
                if (viewName.startsWith("forward:"))  return null; // Laisse Spring gérer le forward
                return noOpView(); // Toute autre vue -> no-op (évite Thymeleaf)
            }
            @Override public int getOrder() { return Ordered.HIGHEST_PRECEDENCE; }
        }

        @Bean(name = "thymeleafViewResolver")
        @Primary
        ViewResolver thymeleafViewResolver() {
            return new HighPriorityNoOpResolver();
        }

        @Bean
        @Primary
        ViewResolver viewResolver() {
            return new HighPriorityNoOpResolver();
        }
    }


    @BeforeEach
    void setUp() {
        given(patientService.findAll(nullable(String.class)))
                .willReturn(List.of(new Patient()));

        Patient p = new Patient(); p.setId(1L);
        given(patientService.getOne(eq(1L), nullable(String.class))).willReturn(p);
        given(noteService.findByPatient(eq(1L), nullable(String.class))).willReturn(List.of());

        RiskAssessmentResponse risk = new RiskAssessmentResponse();
        risk.setPatientId(1);
        risk.setRiskLevel("NONE");
        risk.setTriggerCount(0);
        given(riskService.getRisk(eq(1L), nullable(String.class))).willReturn(risk);

        Patient saved = new Patient(); saved.setId(42L);
        given(patientService.create(any(Patient.class), nullable(String.class))).willReturn(saved);

    }

    @Test
    void list_returns_200() throws Exception {
        mvc.perform(get("/ui/patients"))
           .andExpect(status().isOk());
    }

    @Test
    void details_returns_200() throws Exception {
        mvc.perform(get("/ui/patients/1"))
           .andExpect(status().isOk());
    }

    @Test
    void showAddForm_returns_200() throws Exception {
        mvc.perform(get("/ui/patients/add"))
           .andExpect(status().isOk());
    }

    @Test
    void showEditForm_returns_200() throws Exception {
        mvc.perform(get("/ui/patients/1/edit"))
           .andExpect(status().isOk());
    }

    @Test
    void riskReport_returns_200() throws Exception {
        mvc.perform(get("/ui/patients/1/risk"))
           .andExpect(status().isOk());
    }

    @Test
    void create_redirects_to_details() throws Exception {
        mvc.perform(post("/ui/patients").with(csrf())
                .param("firstName", "Bob")
                .param("lastName", "Martin"))
           .andExpect(status().is3xxRedirection())
           .andExpect(redirectedUrl("/ui/patients/42"));
    }

    @Test
    void update_redirects_to_details() throws Exception {
        mvc.perform(post("/ui/patients/1").with(csrf())
                .param("firstName", "Alice")
                .param("lastName", "Doe"))
           .andExpect(status().is3xxRedirection())
           .andExpect(redirectedUrl("/ui/patients/1"));
    }

    @Test
    void delete_redirects_to_list() throws Exception {
        mvc.perform(post("/ui/patients/1/delete").with(csrf()))
           .andExpect(status().is3xxRedirection())
           .andExpect(redirectedUrl("/ui/patients"));
    }
}
