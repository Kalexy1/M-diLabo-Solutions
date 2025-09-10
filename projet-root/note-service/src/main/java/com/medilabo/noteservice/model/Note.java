package com.medilabo.noteservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Représente une note médicale associée à un patient.
 * <p>
 * Cette entité est stockée dans la collection MongoDB nommée <code>notes</code>.
 * </p>
 * 
 */
@Document(collection = "notes")
public class Note {

    /**
     * Identifiant unique de la note (généré par MongoDB).
     */
    @Id
    private String id;

    /**
     * Identifiant du patient auquel est associée cette note.
     */
    private Integer patientId;

    /**
     * Contenu textuel de la note médicale.
     */
    private String contenu;

    /**
     * Constructeur par défaut requis par Spring Data.
     */
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

    /**
     * Retourne l'identifiant unique de la note.
     *
     * @return l'identifiant de la note
     */
    public String getId() {
        return id;
    }

    /**
     * Retourne l'identifiant du patient lié à cette note.
     *
     * @return l'identifiant du patient
     */
    public Integer getPatientId() {
        return patientId;
    }

    /**
     * Retourne le contenu de la note médicale.
     *
     * @return le contenu de la note
     */
    public String getContenu() {
        return contenu;
    }

    /**
     * Définit l'identifiant de la note.
     *
     * @param id identifiant de la note
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Définit l'identifiant du patient.
     *
     * @param patientId identifiant du patient
     */
    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    /**
     * Définit le contenu de la note.
     *
     * @param contenu texte de la note médicale
     */
    public void setContenu(String contenu) {
        this.contenu = contenu;
    }
}
