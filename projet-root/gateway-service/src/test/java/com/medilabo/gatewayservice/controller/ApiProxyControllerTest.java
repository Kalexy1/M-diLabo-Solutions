package com.medilabo.gatewayservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.lang.reflect.Field;
import java.net.URI;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.medilabo.gatewayservice.TestSecurityConfig;

@WebMvcTest(ApiProxyController.class)
@Import(TestSecurityConfig.class)
class ApiProxyControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ApiProxyController controller;

    @MockBean
    private RestTemplate restTemplate; // remplace le new RestTemplate() interne

    @BeforeEach
    void setup() throws Exception {
        // Remplace le RestTemplate créé dans ApiProxyController par notre mock
        Field f = ApiProxyController.class.getDeclaredField("restTemplate");
        f.setAccessible(true);
        f.set(controller, restTemplate);
    }

    // ----------------------------------------------------------------------
    // 1. SUCCESS : /api/patients routé → patient-service mocké
    // ----------------------------------------------------------------------
    @Test
    void testProxyPatientsSuccess() throws Exception {

        byte[] responseBody = "OK_BACKEND".getBytes();

        ResponseEntity<byte[]> backendResponse =
                new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(restTemplate.exchange(
                any(URI.class),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(byte[].class)
        )).thenReturn(backendResponse);

        mvc.perform(get("/api/patients"))
                .andExpect(status().isOk())
                .andExpect(content().string("OK_BACKEND"));
    }

    // ----------------------------------------------------------------------
    // 2. BACKEND renvoie une erreur 404 → HttpStatusCodeException
    // ----------------------------------------------------------------------
    @Test
    void testProxyBackend404() throws Exception {

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Test", "yes");

        HttpStatusCodeException ex = new HttpStatusCodeException(
                HttpStatus.NOT_FOUND,
                "Not Found",
                headers,
                "NOT_FOUND_BODY".getBytes(),
                null) {};

        when(restTemplate.exchange(
                any(URI.class),
                any(),
                any(),
                eq(byte[].class)
        )).thenThrow(ex);

        mvc.perform(get("/api/patients"))
                .andExpect(status().isNotFound())
                .andExpect(header().string("X-Test", "yes"))
                .andExpect(content().string("NOT_FOUND_BODY"));
    }

    // ----------------------------------------------------------------------
    // 3. BACKEND inaccessible → ResourceAccessException
    // ----------------------------------------------------------------------
    @Test
    void testProxyBackendUnavailable() throws Exception {

        when(restTemplate.exchange(any(URI.class),
                any(), any(), eq(byte[].class)))
                .thenThrow(new ResourceAccessException("Connection failed"));

        mvc.perform(get("/api/patients"))
                .andExpect(status().isBadGateway())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Bad Gateway")));
    }
}
