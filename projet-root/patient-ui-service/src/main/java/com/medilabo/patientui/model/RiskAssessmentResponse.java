package com.medilabo.patientui.model;

/**
 * Représente la réponse renvoyée par le microservice
 * <strong>risk-assessment-service</strong> concernant l’évaluation du risque
 * de diabète d’un patient.
 *
 * <p>
 * Ce DTO (Data Transfer Object) est utilisé par le microservice
 * <strong>patient-ui-service</strong> pour consommer la réponse JSON transmise
 * via la Gateway et afficher le rapport de risque dans l’interface utilisateur.
 * </p>
 */
public class RiskAssessmentResponse {

    /** Identifiant unique du patient. */
    private Long patientId;

    /** Prénom du patient. */
    private String firstName;

    /** Nom de famille du patient. */
    private String lastName;

    /** Âge du patient, calculé par le microservice risk-assessment. */
    private int age;

    /**
     * Niveau de risque détecté pour le patient
     * (ex. : {@code None}, {@code Borderline}, {@code In Danger}, {@code Early onset}).
     */
    private String riskLevel;

    /**
     * Constructeur sans argument requis pour la désérialisation JSON
     * (notamment par Jackson).
     */
    public RiskAssessmentResponse() {
    }

    /** @return identifiant du patient */
    public Long getPatientId() {
        return patientId;
    }

    /** @param patientId identifiant du patient */
    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    /** @return prénom du patient */
    public String getFirstName() {
        return firstName;
    }

    /** @param firstName prénom du patient */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /** @return nom de famille du patient */
    public String getLastName() {
        return lastName;
    }

    /** @param lastName nom de famille du patient */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /** @return âge du patient */
    public int getAge() {
        return age;
    }

    /** @param age âge du patient */
    public void setAge(int age) {
        this.age = age;
    }

    /** @return niveau de risque détecté */
    public String getRiskLevel() {
        return riskLevel;
    }

    /** @param riskLevel niveau de risque détecté */
    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }
}
