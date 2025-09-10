package com.medilabo.riskassessment.dto;

/**
 * DTO représentant une note médicale issue du microservice note-service.
 * <p>
 * Utilisé par le microservice risk-assessment pour analyser le contenu des notes
 * et déterminer le niveau de risque de diabète du patient.
 * </p>
 */
public class NoteDTO {

    /**
     * Identifiant unique de la note (généré par MongoDB).
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
        // Constructeur par défaut
    }

    /**
     * Constructeur complet.
     *
     * @param id l'identifiant de la note
     * @param patientId l'identifiant du patient
     * @param contenu le texte de la note
     */
    public NoteDTO(String id, Integer patientId, String contenu) {
        this.id = id;
        this.patientId = patientId;
        this.contenu = contenu;
    }

    /**
     * @return l'identifiant de la note
     */
    public String getId() {
        return id;
    }

    /**
     * @param id l'identifiant de la note à définir
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return l'identifiant du patient
     */
    public Integer getPatientId() {
        return patientId;
    }

    /**
     * @param patientId l'identifiant du patient à définir
     */
    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    /**
     * @return le contenu textuel de la note
     */
    public String getContenu() {
        return contenu;
    }

    /**
     * @param contenu le texte de la note à définir
     */
    public void setContenu(String contenu) {
        this.contenu = contenu;
    }
}
