package com.medilabo.riskassessment.dto;

/**
 * Représente la réponse retournée par le microservice
 * <strong>risk-assessment-service</strong> après l’évaluation
 * du risque de diabète d’un patient.
 * <p>
 * Cette classe contient les informations essentielles du patient
 * ainsi que son niveau de risque calculé selon :
 * <ul>
 *     <li>son âge,</li>
 *     <li>son sexe,</li>
 *     <li>et la présence de termes déclencheurs dans ses notes médicales.</li>
 * </ul>
 * </p>
 */
public class RiskAssessmentResponse {

    /**
     * Identifiant unique du patient concerné par l’évaluation.
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
     * Niveau de risque évalué.
     * <p>
     * Exemples de valeurs possibles :
     * <ul>
     *     <li>{@code None} — Aucun risque détecté</li>
     *     <li>{@code Borderline} — Risque limité</li>
     *     <li>{@code In Danger} — Patient en danger</li>
     *     <li>{@code Early onset} — Apparition précoce du diabète</li>
     * </ul>
     * </p>
     */
    private String riskLevel;

    /**
     * Constructeur par défaut requis pour la sérialisation/désérialisation JSON.
     */
    public RiskAssessmentResponse() {
    }

    /**
     * Constructeur complet pour initialiser toutes les propriétés.
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
     * Retourne le niveau de risque évalué.
     *
     * @return niveau de risque
     */
    public String getRiskLevel() {
        return riskLevel;
    }

    /**
     * Définit le niveau de risque évalué.
     *
     * @param riskLevel niveau de risque
     */
    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    /**
     * Fournit une représentation textuelle de l’objet, utile pour
     * le débogage et le logging.
     *
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
