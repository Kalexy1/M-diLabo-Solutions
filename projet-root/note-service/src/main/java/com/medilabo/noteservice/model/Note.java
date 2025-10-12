package com.medilabo.noteservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * Représente une note médicale stockée dans la base de données MongoDB.
 * <p>
 * Chaque note est associée à un patient via {@code patientId} et contient
 * un contenu textuel ainsi que des métadonnées temporelles.
 * </p>
 */
@Document(collection = "notes")
public class Note {

    /**
     * Identifiant unique de la note.
     */
    @Id
    private Long id;

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
     * Constructeur par défaut requis pour l’instanciation automatique par Spring Data.
     */
    public Note() {}

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
     * @param id l’identifiant de la note
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
     * Retourne le contenu textuel de la note.
     *
     * @return le contenu de la note
     */
    public String getContent() {
        return content;
    }

    /**
     * Définit le contenu textuel de la note.
     *
     * @param content le contenu à définir
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
     * @param createdAt la date de création
     */
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Retourne la date de dernière mise à jour de la note.
     *
     * @return la date de dernière mise à jour
     */
    public Instant getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Définit la date de dernière mise à jour de la note.
     *
     * @param updatedAt la date de mise à jour
     */
    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
