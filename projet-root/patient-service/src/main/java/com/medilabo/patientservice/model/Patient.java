package com.medilabo.patientservice.model;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Représente un patient dans le système.
 * <p>
 * Cette entité est persistée en base de données via JPA.
 * </p>
 */
@Entity
public class Patient {

    /**
     * Identifiant unique du patient.
     * Généré automatiquement par la base de données.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
     * Date de naissance du patient.
     */
    private LocalDate dateNaissance;

    /**
     * Genre du patient (par exemple : "M" ou "F").
     */
    private String genre;

    /**
     * Adresse postale du patient.
     */
    private String adresse;

    /**
     * Numéro de téléphone du patient.
     */
    private String telephone;

    /**
     * Constructeur sans argument requis par JPA.
     */
    public Patient() {}

    /**
     * Constructeur avec tous les champs.
     *
     * @param id l'identifiant du patient
     * @param prenom le prénom
     * @param nom le nom de famille
     * @param dateNaissance la date de naissance
     * @param genre le genre
     * @param adresse l'adresse
     * @param telephone le numéro de téléphone
     */
    public Patient(Long id, String prenom, String nom, LocalDate dateNaissance, String genre, String adresse, String telephone) {
        this.id = id;
        this.prenom = prenom;
        this.nom = nom;
        this.dateNaissance = dateNaissance;
        this.genre = genre;
        this.adresse = adresse;
        this.telephone = telephone;
    }

    // Getters and setters

    /**
     * @return l'identifiant du patient
     */
    public Long getId() { return id; }

    /**
     * @param id l'identifiant du patient
     */
    public void setId(Long id) { this.id = id; }

    /**
     * @return le prénom du patient
     */
    public String getPrenom() { return prenom; }

    /**
     * @param prenom le prénom du patient
     */
    public void setPrenom(String prenom) { this.prenom = prenom; }

    /**
     * @return le nom du patient
     */
    public String getNom() { return nom; }

    /**
     * @param nom le nom du patient
     */
    public void setNom(String nom) { this.nom = nom; }

    /**
     * @return la date de naissance du patient
     */
    public LocalDate getDateNaissance() { return dateNaissance; }

    /**
     * @param dateNaissance la date de naissance du patient
     */
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }

    /**
     * @return le genre du patient
     */
    public String getGenre() { return genre; }

    /**
     * @param genre le genre du patient
     */
    public void setGenre(String genre) { this.genre = genre; }

    /**
     * @return l'adresse du patient
     */
    public String getAdresse() { return adresse; }

    /**
     * @param adresse l'adresse du patient
     */
    public void setAdresse(String adresse) { this.adresse = adresse; }

    /**
     * @return le numéro de téléphone du patient
     */
    public String getTelephone() { return telephone; }

    /**
     * @param telephone le numéro de téléphone du patient
     */
    public void setTelephone(String telephone) { this.telephone = telephone; }
}
