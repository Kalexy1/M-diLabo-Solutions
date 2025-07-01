package com.medilabo.riskassessment.dto;

/**
 * DTO représentant la réponse du microservice risk-assessment.
 * <p>
 * Cette réponse contient les informations essentielles du patient
 * ainsi que son niveau de risque de diabète évalué.
 * </p>
 */
public class RiskAssessmentResponse {

    /**
     * Identifiant du patient concerné.
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
     * Âge du patient, calculé à partir de sa date de naissance.
     */
    private int age;

    /**
     * Niveau de risque évalué (ex. : "Aucun risque", "Risque léger", "Risque élevé").
     */
    private String riskLevel;

    /**
     * Constructeur par défaut requis pour la sérialisation/désérialisation.
     */
    public RiskAssessmentResponse() {
    }

    /**
     * Constructeur complet.
     *
     * @param patientId identifiant du patient
     * @param firstName prénom du patient
     * @param lastName nom du patient
     * @param age âge du patient
     * @param riskLevel niveau de risque évalué
     */
    public RiskAssessmentResponse(Long patientId, String firstName, String lastName, int age, String riskLevel) {
        this.patientId = patientId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.riskLevel = riskLevel;
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
     * @return l’âge du patient
     */
    public int getAge() {
        return age;
    }

    /**
     * @param age l’âge du patient à définir
     */
    public void setAge(int age) {
        this.age = age;
    }

    /**
     * @return le niveau de risque évalué
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

    /**
     * Fournit une représentation textuelle de l’objet pour le logging ou le débogage.
     *
     * @return une chaîne décrivant la réponse d’évaluation du risque
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
