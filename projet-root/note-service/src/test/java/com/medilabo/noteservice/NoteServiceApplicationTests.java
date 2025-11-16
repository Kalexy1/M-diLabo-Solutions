package com.medilabo.noteservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.containers.MongoDBContainer;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
class NoteServiceApplicationTests {

  /** Mongo éphémère pour les tests */
  @Container
  static final MongoDBContainer mongo = new MongoDBContainer("mongo:7");

  /** Injecte l’URI MongoDB dans le contexte Spring */
  @DynamicPropertySource
  static void mongoProps(DynamicPropertyRegistry registry) {
    registry.add("spring.data.mongodb.uri", mongo::getReplicaSetUrl);
  }

  /** Mocke le décoder JWT pour éviter une config RSA/JWK pendant les tests */
  @MockBean
  JwtDecoder jwtDecoder;

  @Test
  void contextLoads() {
    // Si on arrive ici, le contexte démarre correctement.
  }
}
