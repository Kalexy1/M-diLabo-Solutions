package com.medilabo.riskassessment.dto;

/**
 * Application: com.medilabo.riskassessment.dto
 * <p>
 * Classe <strong>NoteDTO</strong>.
 * <br/>
 * Rôle : Représente une note médicale issue du microservice <em>note-service</em>,
 * utilisée par le microservice <em>risk-assessment-service</em> pour analyser
 * le contenu médical et déterminer le niveau de risque de diabète d’un patient.
 * </p>
 */
public class NoteDTO {

    /**
     * Identifiant unique de la note (généré par MongoDB côté note-service).
     */
    private String id;

    /**
     * Identifiant du patient auquel cette note est associée.
     */
    private Integer patientId;

    /**
     * Contenu textuel de la note médicale.
     */
    private String contenu;

    /**
     * Constructeur par défaut requis pour la désérialisation JSON.
     */
    public NoteDTO() {
        // Constructeur vide requis par les frameworks (Jackson, etc.)
    }

    /**
     * Constructeur complet.
     *
     * @param id        identifiant unique de la note
     * @param patientId identifiant du patient associé
     * @param contenu   texte de la note médicale
     */
    public NoteDTO(String id, Integer patientId, String contenu) {
        this.id = id;
        this.patientId = patientId;
        this.contenu = contenu;
    }

    /**
     * Constructeur simplifié pour créer une note uniquement à partir de son contenu.
     *
     * @param contenu texte de la note médicale
     */
    public NoteDTO(String contenu) {
        this.contenu = contenu;
    }

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
     * Retourne l’identifiant du patient associé à cette note.
     *
     * @return identifiant du patient
     */
    public Integer getPatientId() {
        return patientId;
    }

    /**
     * Définit l’identifiant du patient associé à cette note.
     *
     * @param patientId identifiant du patient
     */
    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    /**
     * Retourne le contenu textuel de la note.
     *
     * @return texte de la note médicale
     */
    public String getContenu() {
        return contenu;
    }

    /**
     * Définit le contenu textuel de la note.
     *
     * @param contenu texte de la note médicale
     */
    public void setContenu(String contenu) {
        this.contenu = contenu;
    }
}
