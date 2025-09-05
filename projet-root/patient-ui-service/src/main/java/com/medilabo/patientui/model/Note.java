package com.medilabo.patientui.model;

/**
 * Application: com.medilabo.patientui.model
 * <p>
 * Classe <strong>Note</strong>.
 * <br/>
 * Rôle : Représente une note médicale associée à un patient dans le
 * microservice <em>patient-ui-service</em>.
 * </p>
 * <p>
 * Cette classe n’est pas une entité persistée localement : elle sert
 * uniquement de modèle pour afficher dans l’UI les données
 * récupérées depuis le microservice <em>note-service</em>.
 * </p>
 */
public class Note {

    /**
     * Identifiant unique de la note (généré par MongoDB
     * côté microservice note-service).
     */
    private String id;

    /**
     * Identifiant du patient auquel la note est associée.
     */
    private Long patientId;

    /**
     * Contenu textuel de la note médicale.
     */
    private String contenu;

    // --- Getters et setters ---

    /**
     * Retourne l’identifiant unique de la note.
     *
     * @return identifiant de la note
     */
    public String getId() {
        return id;
    }

    /**
     * Définit l’identifiant unique de la note.
     *
     * @param id identifiant de la note
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Retourne l’identifiant du patient associé à la note.
     *
     * @return identifiant du patient
     */
    public Long getPatientId() {
        return patientId;
    }

    /**
     * Définit l’identifiant du patient associé à la note.
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
    public String getContenu() {
        return contenu;
    }

    /**
     * Définit le contenu textuel de la note.
     *
     * @param contenu contenu de la note
     */
    public void setContenu(String contenu) {
        this.contenu = contenu;
    }
}
