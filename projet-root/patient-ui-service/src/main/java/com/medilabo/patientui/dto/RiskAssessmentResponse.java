package com.medilabo.patientui.dto;

/**
 * Objet de transfert de données (DTO) représentant la réponse du microservice
 * risk-assessment concernant le niveau de risque d'un patient.
 */
public class RiskAssessmentResponse {

    /**
     * Identifiant du patient.
     */
    private Long patientId;

    /**
     * Prénom du patient.
     */
    private String firstName;

    /**
     * Nom du patient.
     */
    private String lastName;

    /**
     * Âge du patient.
     */
    private int age;

    /**
     * Niveau de risque détecté pour le patient (par exemple : "Aucun risque", "Risque faible", etc.).
     */
    private String riskLevel;

    /**
     * Constructeur sans argument requis pour la désérialisation JSON.
     */
    public RiskAssessmentResponse() {
    }

    /**
     * @return l'identifiant du patient
     */
    public Long getPatientId() {
        return patientId;
    }

    /**
     * @param patientId l'identifiant du patient à définir
     */
    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    /**
     * @return le prénom du patient
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName le prénom du patient à définir
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return le nom du patient
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName le nom du patient à définir
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return l'âge du patient
     */
    public int getAge() {
        return age;
    }

    /**
     * @param age l'âge du patient à définir
     */
    public void setAge(int age) {
        this.age = age;
    }

    /**
     * @return le niveau de risque détecté
     */
    public String getRiskLevel() {
        return riskLevel;
    }

    /**
     * @param riskLevel le niveau de risque à définir
     */
    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }
}
