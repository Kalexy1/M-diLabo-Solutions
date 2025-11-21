package com.medilabo.noteservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * Représente une note médicale stockée dans MongoDB.
 *
 * <p>Chaque note est liée à un patient via {@code patientId} et contient un texte
 * libre ainsi que deux marqueurs temporels indiquant la création et la mise à jour
 * de la note.</p>
 */
@Document(collection = "notes")
public class Note {

    /**
     * Identifiant unique de la note (ObjectId MongoDB sous forme de chaîne).
     */
    @Id
    private String id;

    /**
     * Identifiant du patient associé à la note.
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
     * Constructeur sans argument requis par Spring Data.
     */
    public Note() {}

    /**
     * Retourne l'identifiant unique de la note.
     *
     * @return identifiant de la note
     */
    public String getId() {
        return id;
    }

    /**
     * Définit l'identifiant unique de la note.
     *
     * @param id identifiant à définir
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Retourne l'identifiant du patient associé.
     *
     * @return identifiant du patient
     */
    public Long getPatientId() {
        return patientId;
    }

    /**
     * Définit l'identifiant du patient associé.
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
     * @return timestamp de création
     */
    public Instant getCreatedAt() {
        return createdAt;
    }

    /**
     * Définit la date de création de la note.
     *
     * @param createdAt timestamp de création
     */
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Retourne la date de dernière mise à jour.
     *
     * @return timestamp de mise à jour
     */
    public Instant getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Définit la date de dernière mise à jour de la note.
     *
     * @param updatedAt timestamp de mise à jour
     */
    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
