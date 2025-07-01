package com.medilabo.riskassessment.dto;

import java.time.LocalDate;

/**
 * DTO représentant un patient, utilisé pour transférer les données
 * depuis le microservice patient-service vers le microservice risk-assessment.
 * <p>
 * Ces informations sont utilisées pour calculer l'âge du patient et déterminer le niveau de risque.
 * </p>
 */
public class PatientDTO {

    /**
     * Identifiant unique du patient.
     */
    private Long id;

    /**
     * Prénom du patient.
     */
    private String prenom;

    /**
     * Nom du patient.
     */
    private String nom;

    /**
     * Genre du patient (ex. : "M", "F").
     */
    private String genre;

    /**
     * Date de naissance du patient.
     */
    private LocalDate dateNaissance;

    /**
     * @return l'identifiant du patient
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id l'identifiant du patient à définir
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return le prénom du patient
     */
    public String getPrenom() {
        return prenom;
    }

    /**
     * @param prenom le prénom du patient à définir
     */
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    /**
     * @return le nom du patient
     */
    public String getNom() {
        return nom;
    }

    /**
     * @param nom le nom du patient à définir
     */
    public void setNom(String nom) {
        this.nom = nom;
    }

    /**
     * @return le genre du patient
     */
    public String getGenre() {
        return genre;
    }

    /**
     * @param genre le genre du patient à définir
     */
    public void setGenre(String genre) {
        this.genre = genre;
    }

    /**
     * @return la date de naissance du patient
     */
    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    /**
     * @param dateNaissance la date de naissance à définir
     */
    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }
}
