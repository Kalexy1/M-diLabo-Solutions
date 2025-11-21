package com.medilabo.riskassessment.dto;

import java.time.Instant;

/**
 * DTO représentant une note médicale transmise par le microservice
 * <strong>note-service</strong> au microservice
 * <strong>risk-assessment-service</strong>.
 *
 * <p>
 * Ce modèle est utilisé pour l’analyse du contenu textuel des notes
 * lors du calcul du risque de diabète.
 * </p>
 */
public class NoteDTO {

    /**
     * Identifiant unique de la note (ObjectId MongoDB sérialisé en chaîne).
     */
    private String id;

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
     * Constructeur par défaut requis pour la désérialisation JSON.
     */
    public NoteDTO() {}

    /**
     * Constructeur complet.
     *
     * @param id identifiant unique de la note
     * @param patientId identifiant du patient associé
     * @param content contenu textuel de la note
     * @param createdAt date de création de la note
     * @param updatedAt date de dernière mise à jour
     */
    public NoteDTO(String id, Long patientId, String content, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.patientId = patientId;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Constructeur simplifié basé uniquement sur le contenu.
     *
     * @param content contenu textuel de la note
     */
    public NoteDTO(String content) {
        this.content = content;
    }

    /**
     * Retourne l’identifiant de la note.
     *
     * @return identifiant de la note
     */
    public String getId() {
        return id;
    }

    /**
     * Définit l’identifiant de la note.
     *
     * @param id identifiant de la note
     */
    public void setId(String id) {
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
     * @param content texte de la note
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Retourne la date de création de la note.
     *
     * @return date de création
     */
    public Instant getCreatedAt() {
        return createdAt;
    }

    /**
     * Définit la date de création de la note.
     *
     * @param createdAt date de création
     */
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Retourne la date de dernière mise à jour.
     *
     * @return date de mise à jour
     */
    public Instant getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Définit la date de dernière mise à jour.
     *
     * @param updatedAt date de mise à jour
     */
    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
