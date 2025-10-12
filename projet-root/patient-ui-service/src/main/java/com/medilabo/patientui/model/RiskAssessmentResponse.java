package com.medilabo.patientui.model;

/**
 * Représente la réponse du microservice d’évaluation du risque de diabète.
 * <p>
 * Ce DTO (Data Transfer Object) est utilisé par le microservice
 * <strong>patient-ui-service</strong> pour afficher le niveau de risque
 * d’un patient dans l’interface utilisateur. Il correspond à la réponse
 * fournie par le <strong>risk-assessment-service</strong>.
 * </p>
 */
public class RiskAssessmentResponse {

    /**
     * Identifiant du patient concerné par l’évaluation.
     */
    private Integer patientId;

    /**
     * Niveau de risque détecté (exemples : {@code NONE}, {@code BORDERLINE},
     * {@code IN_DANGER}, {@code EARLY_ONSET}).
     */
    private String riskLevel;

    /**
     * Nombre de déclencheurs (mots clés médicaux) détectés dans les notes du patient.
     */
    private int triggerCount;

    /**
     * Retourne l’identifiant du patient concerné.
     *
     * @return l’identifiant du patient
     */
    public Integer getPatientId() {
        return patientId;
    }

    /**
     * Définit l’identifiant du patient concerné.
     *
     * @param patientId l’identifiant du patient
     */
    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    /**
     * Retourne le niveau de risque détecté.
     *
     * @return le niveau de risque
     */
    public String getRiskLevel() {
        return riskLevel;
    }

    /**
     * Définit le niveau de risque détecté.
     *
     * @param riskLevel le niveau de risque à définir
     */
    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    /**
     * Retourne le nombre de déclencheurs médicaux détectés.
     *
     * @return le nombre de déclencheurs
     */
    public int getTriggerCount() {
        return triggerCount;
    }

    /**
     * Définit le nombre de déclencheurs médicaux détectés.
     *
     * @param triggerCount le nombre de déclencheurs à définir
     */
    public void setTriggerCount(int triggerCount) {
        this.triggerCount = triggerCount;
    }
}
