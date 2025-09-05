package com.medilabo.patientservice.model;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Application: com.medilabo.patientservice.model
 * <p>
 * Classe <strong>Patient</strong>.
 * <br/>
 * Rôle : Entité JPA représentant un patient dans le système.
 * </p>
 * <p>
 * Cette entité est persistée en base de données via JPA/Hibernate.
 * </p>
 */
@Entity
public class Patient {

    /** Identifiant unique du patient, généré automatiquement. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Prénom du patient. */
    private String prenom;

    /** Nom de famille du patient. */
    private String nom;

    /** Date de naissance du patient. */
    private LocalDate dateNaissance;

    /** Genre du patient (par ex. "M" ou "F"). */
    private String genre;

    /** Adresse postale du patient. */
    private String adresse;

    /** Numéro de téléphone du patient. */
    private String telephone;

    /** Constructeur sans argument requis par JPA. */
    public Patient() {}

    /**
     * Constructeur complet.
     *
     * @param id identifiant du patient
     * @param prenom prénom du patient
     * @param nom nom du patient
     * @param dateNaissance date de naissance
     * @param genre genre
     * @param adresse adresse postale
     * @param telephone numéro de téléphone
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

    /**
     * Constructeur sans identifiant (pour création).
     *
     * @param prenom prénom du patient
     * @param nom nom du patient
     * @param dateNaissance date de naissance
     * @param genre genre
     * @param adresse adresse postale
     * @param telephone numéro de téléphone
     */
    public Patient(String prenom, String nom, LocalDate dateNaissance, String genre, String adresse, String telephone) {
        this.prenom = prenom;
        this.nom = nom;
        this.dateNaissance = dateNaissance;
        this.genre = genre;
        this.adresse = adresse;
        this.telephone = telephone;
    }

    /** @return identifiant du patient */
    public Long getId() { return id; }
    /** @param id identifiant du patient */
    public void setId(Long id) { this.id = id; }

    /** @return prénom du patient */
    public String getPrenom() { return prenom; }
    /** @param prenom prénom du patient */
    public void setPrenom(String prenom) { this.prenom = prenom; }

    /** @return nom du patient */
    public String getNom() { return nom; }
    /** @param nom nom du patient */
    public void setNom(String nom) { this.nom = nom; }

    /** @return date de naissance du patient */
    public LocalDate getDateNaissance() { return dateNaissance; }
    /** @param dateNaissance date de naissance du patient */
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }

    /** @return genre du patient */
    public String getGenre() { return genre; }
    /** @param genre genre du patient */
    public void setGenre(String genre) { this.genre = genre; }

    /** @return adresse du patient */
    public String getAdresse() { return adresse; }
    /** @param adresse adresse du patient */
    public void setAdresse(String adresse) { this.adresse = adresse; }

    /** @return numéro de téléphone du patient */
    public String getTelephone() { return telephone; }
    /** @param telephone numéro de téléphone du patient */
    public void setTelephone(String telephone) { this.telephone = telephone; }
}
