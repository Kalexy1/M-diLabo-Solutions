package com.medilabo.riskassessment.dto;

import java.time.LocalDate;

/**
 * Application: com.medilabo.riskassessment.dto
 * <p>
 * Classe <strong>PatientDTO</strong>.
 * <br/>
 * Rôle : Représente un patient transféré depuis le microservice <em>patient-service</em>
 * vers le microservice <em>risk-assessment-service</em>.
 * </p>
 * <p>
 * Les informations contenues dans ce DTO permettent notamment de :
 * <ul>
 *   <li>Calculer l’âge du patient à partir de sa {@link #dateNaissance}.</li>
 *   <li>Déterminer son niveau de risque de diabète en combinaison avec ses notes médicales.</li>
 * </ul>
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
     * Nom de famille du patient.
     */
    private String nom;

    /**
     * Genre du patient (par exemple : {@code "M"} ou {@code "F"}).
     */
    private String genre;

    /**
     * Date de naissance du patient.
     */
    private LocalDate dateNaissance;

    /**
     * Retourne l’identifiant du patient.
     *
     * @return identifiant unique du patient
     */
    public Long getId() {
        return id;
    }

    /**
     * Définit l’identifiant du patient.
     *
     * @param id identifiant unique à définir
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retourne le prénom du patient.
     *
     * @return prénom du patient
     */
    public String getPrenom() {
        return prenom;
    }

    /**
     * Définit le prénom du patient.
     *
     * @param prenom prénom à définir
     */
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    /**
     * Retourne le nom de famille du patient.
     *
     * @return nom du patient
     */
    public String getNom() {
        return nom;
    }

    /**
     * Définit le nom de famille du patient.
     *
     * @param nom nom à définir
     */
    public void setNom(String nom) {
        this.nom = nom;
    }

    /**
     * Retourne le genre du patient.
     *
     * @return genre du patient
     */
    public String getGenre() {
        return genre;
    }

    /**
     * Définit le genre du patient.
     *
     * @param genre genre à définir
     */
    public void setGenre(String genre) {
        this.genre = genre;
    }

    /**
     * Retourne la date de naissance du patient.
     *
     * @return date de naissance
     */
    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    /**
     * Définit la date de naissance du patient.
     *
     * @param dateNaissance date de naissance à définir
     */
    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    /**
     * Constructeur complet.
     *
     * @param id            identifiant unique du patient
     * @param prenom        prénom du patient
     * @param nom           nom de famille du patient
     * @param dateNaissance date de naissance du patient
     * @param genre         genre du patient
     */
    public PatientDTO(Long id, String prenom, String nom, LocalDate dateNaissance, String genre) {
        this.id = id;
        this.prenom = prenom;
        this.nom = nom;
        this.dateNaissance = dateNaissance;
        this.genre = genre;
    }

    /**
     * Constructeur sans arguments.
     * <br/>
     * Requis pour la désérialisation JSON par les frameworks comme Jackson.
     */
    public PatientDTO() {
    }
}
