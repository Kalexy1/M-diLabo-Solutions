package com.medilabo.patientui;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;

import com.medilabo.patientui.service.NoteService;
import com.medilabo.patientui.service.PatientService;
import com.medilabo.patientui.service.RiskService;
import com.medilabo.patientui.repository.PatientRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration(exclude = {
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
        JpaRepositoriesAutoConfiguration.class,
        FlywayAutoConfiguration.class,
        LiquibaseAutoConfiguration.class,
        SecurityAutoConfiguration.class,               // on garde l’exclusion
        OAuth2ResourceServerAutoConfiguration.class
})
@ActiveProfiles("test")
@Import(PatientUiServiceApplicationTests.TestSecurityConfig.class)
class PatientUiServiceApplicationTests {

    // --- faux SecurityFilterChain pour neutraliser SecurityConfig ---
	@TestConfiguration
	static class TestSecurityConfig {

	    @Bean(name = "securityFilterChain")
	    SecurityFilterChain securityFilterChain() {
	        return new SecurityFilterChain() {
	            @Override
	            public boolean matches(jakarta.servlet.http.HttpServletRequest request) {
	                return false; // never matches; keeps chain inert for tests
	            }

	            @Override
	            public java.util.List<jakarta.servlet.Filter> getFilters() {
	                return java.util.Collections.emptyList(); // no filters
	            }
	        };
	    }
	}


    // --- mocks réseau/sécurité ---
    @MockBean JwtDecoder jwtDecoder;
    @MockBean PatientService patientService;
    @MockBean NoteService noteService;
    @MockBean RiskService riskService;

    // --- si un composant autowire PatientRepository, on le remplace ---
    @MockBean PatientRepository patientRepository;

    // --- court-circuit des RestTemplate nommés d’AppConfig (si présents) ---
    @MockBean(name = "patientApiClient") org.springframework.web.client.RestTemplate patientApiClient;
    @MockBean(name = "noteApiClient")    org.springframework.web.client.RestTemplate noteApiClient;
    @MockBean(name = "riskApiClient")    org.springframework.web.client.RestTemplate riskApiClient;

    @Test
    void contextLoads() {}
}
