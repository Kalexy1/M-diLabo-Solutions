package com.medilabo.noteservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Application: com.medilabo.noteservice.model
 * <p>
 * Classe <strong>Note</strong>.
 * <br/>
 * Rôle : Représente une note médicale associée à un patient.
 * </p>
 * <p>
 * Cette entité est stockée dans la collection MongoDB nommée <code>notes</code>.
 * </p>
 */
@Document(collection = "notes")
public class Note {

    /** Identifiant unique de la note (généré par MongoDB). */
    @Id
    private String id;

    /** Identifiant du patient auquel est associée cette note. */
    private Integer patientId;

    /** Contenu textuel de la note médicale. */
    private String contenu;

    /** Constructeur par défaut requis par Spring Data. */
    public Note() {
    }

    /**
     * Constructeur avec tous les champs (sauf l'identifiant auto-généré).
     *
     * @param patientId identifiant du patient
     * @param contenu contenu de la note médicale
     */
    public Note(Integer patientId, String contenu) {
        this.patientId = patientId;
        this.contenu = contenu;
    }

    /** @return l'identifiant unique de la note */
    public String getId() {
        return id;
    }

    /** @return l'identifiant du patient */
    public Integer getPatientId() {
        return patientId;
    }

    /** @return le contenu de la note médicale */
    public String getContenu() {
        return contenu;
    }

    /** @param id identifiant de la note */
    public void setId(String id) {
        this.id = id;
    }

    /** @param patientId identifiant du patient */
    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    /** @param contenu texte de la note médicale */
    public void setContenu(String contenu) {
        this.contenu = contenu;
    }
}
