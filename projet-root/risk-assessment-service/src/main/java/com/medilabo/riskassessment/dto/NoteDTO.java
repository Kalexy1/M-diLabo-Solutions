package com.medilabo.riskassessment.dto;

import java.time.Instant;

/**
 * Représente une note médicale transférée depuis le microservice
 * <strong>note-service</strong> vers le microservice
 * <strong>risk-assessment-service</strong>.
 * <p>
 * Cette classe est utilisée pour l’analyse du contenu médical
 * lors de l’évaluation du risque de diabète.
 * </p>
 */
public class NoteDTO {

    /**
     * Identifiant unique de la note (généré par MongoDB dans note-service).
     */
    private Long id;

    /**
     * Identifiant du patient associé à cette note.
     */
    private Long patientId;

    /**
     * Contenu textuel de la note médicale.
     */
    private String content;

    /**
     * Date et heure de création de la note.
     */
    private Instant createdAt;

    /**
     * Date et heure de dernière mise à jour de la note.
     */
    private Instant updatedAt;

    /**
     * Constructeur par défaut (nécessaire pour la désérialisation JSON).
     */
    public NoteDTO() {}

    /**
     * Constructeur complet.
     *
     * @param id        identifiant unique de la note
     * @param patientId identifiant du patient associé
     * @param content   contenu textuel de la note
     * @param createdAt date et heure de création
     * @param updatedAt date et heure de dernière mise à jour
     */
    public NoteDTO(Long id, Long patientId, String content, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.patientId = patientId;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Constructeur simplifié (pour les notes basées uniquement sur le contenu).
     *
     * @param content contenu textuel de la note
     */
    public NoteDTO(String content) {
        this.content = content;
    }

    /**
     * Retourne l’identifiant de la note.
     *
     * @return l’identifiant de la note
     */
    public Long getId() {
        return id;
    }

    /**
     * Définit l’identifiant de la note.
     *
     * @param id identifiant de la note
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retourne l’identifiant du patient associé.
     *
     * @return identifiant du patient
     */
    public Long getPatientId() {
        return patientId;
    }

    /**
     * Définit l’identifiant du patient associé.
     *
     * @param patientId identifiant du patient
     */
    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    /**
     * Retourne le contenu textuel de la note.
     *
     * @return contenu de la note
     */
    public String getContent() {
        return content;
    }

    /**
     * Définit le contenu textuel de la note.
     *
     * @param content contenu de la note
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Retourne la date et l’heure de création de la note.
     *
     * @return date de création
     */
    public Instant getCreatedAt() {
        return createdAt;
    }

    /**
     * Définit la date et l’heure de création de la note.
     *
     * @param createdAt date de création
     */
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Retourne la date et l’heure de dernière mise à jour.
     *
     * @return date de mise à jour
     */
    public Instant getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Définit la date et l’heure de dernière mise à jour.
     *
     * @param updatedAt date de mise à jour
     */
    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
