package com.medilabo.patientui.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Application: com.medilabo.patientui.model
 * <p>
 * Classe <strong>Patient</strong>.
 * <br/>
 * Rôle : Représente un patient dans le microservice <em>patient-ui-service</em>.
 * </p>
 * <p>
 * Cette entité est stockée dans une base de données relationnelle côté UI,
 * mais ses notes médicales sont récupérées dynamiquement depuis le microservice
 * <em>note-service</em> et ne sont pas persistées localement.
 * </p>
 */
@Entity
public class Patient {

    /**
     * Identifiant unique du patient (clé primaire).
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
     * Genre du patient (exemple : {@code "M"} ou {@code "F"}).
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
     * Liste des notes médicales du patient.
     * <p>
     * Cette propriété est marquée {@link Transient}, donc
     * non persistée en base. Elle est alimentée dynamiquement
     * via des appels au microservice <em>note-service</em>.
     * </p>
     */
    @Transient
    private List<Note> notes;

    // --- Getters et setters ---

    /**
     * Retourne l’identifiant du patient.
     *
     * @return identifiant unique
     */
    public Long getId() { return id; }

    /**
     * Définit l’identifiant du patient.
     *
     * @param id identifiant unique
     */
    public void setId(Long id) { this.id = id; }

    /**
     * Retourne le prénom du patient.
     *
     * @return prénom
     */
    public String getPrenom() { return prenom; }

    /**
     * Définit le prénom du patient.
     *
     * @param prenom prénom
     */
    public void setPrenom(String prenom) { this.prenom = prenom; }

    /**
     * Retourne le nom de famille du patient.
     *
     * @return nom
     */
    public String getNom() { return nom; }

    /**
     * Définit le nom de famille du patient.
     *
     * @param nom nom
     */
    public void setNom(String nom) { this.nom = nom; }

    /**
     * Retourne la date de naissance du patient.
     *
     * @return date de naissance
     */
    public LocalDate getDateNaissance() { return dateNaissance; }

    /**
     * Définit la date de naissance du patient.
     *
     * @param dateNaissance date de naissance
     */
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }

    /**
     * Retourne le genre du patient.
     *
     * @return genre
     */
    public String getGenre() { return genre; }

    /**
     * Définit le genre du patient.
     *
     * @param genre genre
     */
    public void setGenre(String genre) { this.genre = genre; }

    /**
     * Retourne l’adresse postale du patient.
     *
     * @return adresse
     */
    public String getAdresse() { return adresse; }

    /**
     * Définit l’adresse postale du patient.
     *
     * @param adresse adresse
     */
    public void setAdresse(String adresse) { this.adresse = adresse; }

    /**
     * Retourne le numéro de téléphone du patient.
     *
     * @return téléphone
     */
    public String getTelephone() { return telephone; }

    /**
     * Définit le numéro de téléphone du patient.
     *
     * @param telephone téléphone
     */
    public void setTelephone(String telephone) { this.telephone = telephone; }

    /**
     * Retourne la liste des notes médicales associées au patient.
     *
     * @return liste de notes (non persistée)
     */
    public List<Note> getNotes() { return notes; }

    /**
     * Définit la liste des notes médicales associées au patient.
     *
     * @param notes liste de notes (non persistée)
     */
    public void setNotes(List<Note> notes) { this.notes = notes; }
}
