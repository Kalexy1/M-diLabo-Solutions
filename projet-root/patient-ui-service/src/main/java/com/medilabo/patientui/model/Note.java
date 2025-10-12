package com.medilabo.patientui.model;

import java.time.Instant;

/**
 * Représente une note médicale associée à un patient.
 * <p>
 * Cette classe agit comme un DTO (Data Transfer Object) pour la communication
 * entre le microservice <strong>patient-ui-service</strong> et le
 * <strong>note-service</strong>. Elle reflète la structure du modèle de données
 * du service des notes.
 * </p>
 */
public class Note {

    /**
     * Identifiant unique de la note.
     */
    private Long id;

    /**
     * Identifiant du patient auquel la note est rattachée.
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
     * Date et heure de la dernière mise à jour de la note.
     */
    private Instant updatedAt;

    /**
     * Retourne l’identifiant unique de la note.
     *
     * @return l’identifiant de la note
     */
    public Long getId() {
        return id;
    }

    /**
     * Définit l’identifiant unique de la note.
     *
     * @param id l’identifiant à définir
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retourne l’identifiant du patient associé à la note.
     *
     * @return l’identifiant du patient
     */
    public Long getPatientId() {
        return patientId;
    }

    /**
     * Définit l’identifiant du patient associé à la note.
     *
     * @param patientId l’identifiant du patient
     */
    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    /**
     * Retourne le contenu de la note.
     *
     * @return le texte de la note
     */
    public String getContent() {
        return content;
    }

    /**
     * Définit le contenu de la note.
     *
     * @param content le texte de la note
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Retourne la date de création de la note.
     *
     * @return la date de création
     */
    public Instant getCreatedAt() {
        return createdAt;
    }

    /**
     * Définit la date de création de la note.
     *
     * @param createdAt la date de création à définir
     */
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Retourne la date de la dernière mise à jour de la note.
     *
     * @return la date de mise à jour
     */
    public Instant getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Définit la date de la dernière mise à jour de la note.
     *
     * @param updatedAt la date de mise à jour à définir
     */
    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
