package com.medilabo.patientui.dto;

/**
 * Application: com.medilabo.patientui.dto
 * <p>
 * Classe <strong>RiskAssessmentResponse</strong>.
 * <br/>
 * Rôle : Objet de transfert de données (DTO) représentant la réponse du microservice
 * <em>risk-assessment-service</em> concernant l’évaluation du niveau de risque d’un patient.
 * </p>
 * <p>
 * Cette classe est utilisée pour recevoir la réponse JSON du service d’évaluation des risques
 * via la Gateway et l’exploiter dans le microservice <em>patient-ui-service</em>.
 * </p>
 */
public class RiskAssessmentResponse {

    /**
     * Identifiant unique du patient.
     */
    private Long patientId;

    /**
     * Prénom du patient.
     */
    private String firstName;

    /**
     * Nom de famille du patient.
     */
    private String lastName;

    /**
     * Âge du patient (calculé côté risk-assessment).
     */
    private int age;

    /**
     * Niveau de risque détecté pour le patient
     * (par exemple : {@code None}, {@code Borderline}, {@code In Danger}, {@code Early onset}).
     */
    private String riskLevel;

    /**
     * Constructeur sans argument.
     * <br/>
     * Requis pour la désérialisation JSON par les frameworks (ex. Jackson).
     */
    public RiskAssessmentResponse() {
    }

    /**
     * Retourne l’identifiant du patient.
     *
     * @return identifiant du patient
     */
    public Long getPatientId() {
        return patientId;
    }

    /**
     * Définit l’identifiant du patient.
     *
     * @param patientId identifiant du patient
     */
    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    /**
     * Retourne le prénom du patient.
     *
     * @return prénom du patient
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Définit le prénom du patient.
     *
     * @param firstName prénom du patient
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Retourne le nom du patient.
     *
     * @return nom du patient
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Définit le nom du patient.
     *
     * @param lastName nom du patient
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Retourne l’âge du patient.
     *
     * @return âge du patient
     */
    public int getAge() {
        return age;
    }

    /**
     * Définit l’âge du patient.
     *
     * @param age âge du patient
     */
    public void setAge(int age) {
        this.age = age;
    }

    /**
     * Retourne le niveau de risque détecté.
     *
     * @return niveau de risque
     */
    public String getRiskLevel() {
        return riskLevel;
    }

    /**
     * Définit le niveau de risque détecté.
     *
     * @param riskLevel niveau de risque
     */
    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }
}
