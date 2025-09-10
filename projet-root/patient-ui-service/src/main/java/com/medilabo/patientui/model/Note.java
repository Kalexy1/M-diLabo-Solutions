package com.medilabo.patientui.model;

/**
 * Représente une note médicale associée à un patient.
 * <p>
 * Cette classe est utilisée dans l'application front-end (patient-ui)
 * pour afficher les notes récupérées depuis le microservice note-service.
 * </p>
 */
public class Note {

    /**
     * Identifiant unique de la note (généré par MongoDB).
     */
    private String id;

    /**
     * Identifiant du patient auquel la note est liée.
     */
    private Long patientId;

    /**
     * Contenu textuel de la note médicale.
     */
    private String contenu;

    // Getters et setters

    /**
     * @return l'identifiant de la note
     */
    public String getId() { return id; }

    /**
     * @param id l'identifiant de la note à définir
     */
    public void setId(String id) { this.id = id; }

    /**
     * @return l'identifiant du patient associé
     */
    public Long getPatientId() { return patientId; }

    /**
     * @param patientId l'identifiant du patient à associer à la note
     */
    public void setPatientId(Long patientId) { this.patientId = patientId; }

    /**
     * @return le contenu de la note
     */
    public String getContenu() { return contenu; }

    /**
     * @param contenu le contenu textuel de la note à définir
     */
    public void setContenu(String contenu) { this.contenu = contenu; }
}
