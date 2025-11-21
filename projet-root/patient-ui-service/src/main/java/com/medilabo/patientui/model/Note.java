package com.medilabo.patientui.model;

import java.time.Instant;

/**
 * Représente une note médicale associée à un patient.
 *
 * <p>Cette classe joue le rôle de DTO (Data Transfer Object) pour les échanges
 * entre le microservice <strong>patient-ui-service</strong> et le
 * <strong>note-service</strong>. Elle reflète la structure du modèle de données
 * du microservice des notes afin de permettre la sérialisation/désérialisation JSON.</p>
 */
public class Note {

    /**
     * Identifiant unique de la note, correspondant à l'ObjectId MongoDB
     * sérialisé sous forme de chaîne.
     */
    private String id;

    /**
     * Identifiant du patient auquel la note est associée.
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

    /** @return identifiant unique de la note */
    public String getId() {
        return id;
    }

    /** @param id identifiant unique de la note */
    public void setId(String id) {
        this.id = id;
    }

    /** @return identifiant du patient associé */
    public Long getPatientId() {
        return patientId;
    }

    /** @param patientId identifiant du patient associé à la note */
    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    /** @return contenu textuel de la note */
    public String getContent() {
        return content;
    }

    /** @param content contenu textuel de la note */
    public void setContent(String content) {
        this.content = content;
    }

    /** @return date et heure de création de la note */
    public Instant getCreatedAt() {
        return createdAt;
    }

    /** @param createdAt date de création à définir */
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    /** @return date et heure de la dernière mise à jour */
    public Instant getUpdatedAt() {
        return updatedAt;
    }

    /** @param updatedAt date de mise à jour à définir */
    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
