package com.medilabo.riskassessment.dto;

/**
 * Représente la réponse retournée par le microservice
 * <strong>risk-assessment-service</strong> après l’évaluation
 * du risque de diabète d’un patient.
 *
 * <p>
 * Cette réponse regroupe les informations essentielles du patient ainsi
 * que le niveau de risque calculé à partir :
 * </p>
 * <ul>
 *     <li>de son âge,</li>
 *     <li>de son sexe,</li>
 *     <li>et de la présence de termes déclencheurs dans ses notes médicales.</li>
 * </ul>
 */
public class RiskAssessmentResponse {

    /**
     * Identifiant unique du patient évalué.
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
     * Niveau de risque détecté.
     *
     * <p>Exemples de valeurs possibles :</p>
     * <ul>
     *     <li>{@code None}</li>
     *     <li>{@code Borderline}</li>
     *     <li>{@code In Danger}</li>
     *     <li>{@code Early onset}</li>
     * </ul>
     */
    private String riskLevel;

    /**
     * Constructeur sans argument requis pour la (dé)sérialisation JSON.
     */
    public RiskAssessmentResponse() {
    }

    /**
     * Constructeur complet.
     *
     * @param patientId identifiant du patient
     * @param firstName prénom du patient
     * @param lastName  nom du patient
     * @param age       âge du patient
     * @param riskLevel niveau de risque évalué
     */
    public RiskAssessmentResponse(Long patientId, String firstName, String lastName, int age, String riskLevel) {
        this.patientId = patientId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.riskLevel = riskLevel;
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

    /** @return nom du patient */
    public String getLastName() {
        return lastName;
    }

    /** @param lastName nom du patient */
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

    /** @return niveau de risque évalué */
    public String getRiskLevel() {
        return riskLevel;
    }

    /** @param riskLevel niveau de risque évalué */
    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    /**
     * @return représentation textuelle de la réponse de risque
     */
    @Override
    public String toString() {
        return "RiskAssessmentResponse{" +
            "patientId=" + patientId +
            ", firstName='" + firstName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", age=" + age +
            ", riskLevel='" + riskLevel + '\'' +
            '}';
    }
}
