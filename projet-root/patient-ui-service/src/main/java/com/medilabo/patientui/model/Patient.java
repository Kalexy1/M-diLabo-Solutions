package com.medilabo.patientui.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Représente un patient dans l'application Patient UI.
 * <p>
 * Cette entité est stockée dans une base de données relationnelle et affichée
 * avec ses notes médicales dans l'interface utilisateur.
 * Les notes sont récupérées dynamiquement via un microservice externe et ne sont pas stockées localement.
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
     * Genre du patient (ex. : "M", "F").
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
     * Cette liste n’est pas persistée dans la base de données. Elle est renseignée dynamiquement
     * depuis le microservice note-service.
     * </p>
     */
    @Transient
    private List<Note> notes;

    // Getters et setters

    /**
     * @return l'identifiant du patient
     */
    public Long getId() { return id; }

    /**
     * @param id l'identifiant du patient à définir
     */
    public void setId(Long id) { this.id = id; }

    /**
     * @return le prénom du patient
     */
    public String getPrenom() { return prenom; }

    /**
     * @param prenom le prénom du patient à définir
     */
    public void setPrenom(String prenom) { this.prenom = prenom; }

    /**
     * @return le nom du patient
     */
    public String getNom() { return nom; }

    /**
     * @param nom le nom du patient à définir
     */
    public void setNom(String nom) { this.nom = nom; }

    /**
     * @return la date de naissance du patient
     */
    public LocalDate getDateNaissance() { return dateNaissance; }

    /**
     * @param dateNaissance la date de naissance à définir
     */
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }

    /**
     * @return le genre du patient
     */
    public String getGenre() { return genre; }

    /**
     * @param genre le genre du patient à définir
     */
    public void setGenre(String genre) { this.genre = genre; }

    /**
     * @return l'adresse du patient
     */
    public String getAdresse() { return adresse; }

    /**
     * @param adresse l'adresse du patient à définir
     */
    public void setAdresse(String adresse) { this.adresse = adresse; }

    /**
     * @return le numéro de téléphone du patient
     */
    public String getTelephone() { return telephone; }

    /**
     * @param telephone le numéro de téléphone à définir
     */
    public void setTelephone(String telephone) { this.telephone = telephone; }

    /**
     * @return la liste des notes médicales du patient
     */
    public List<Note> getNotes() { return notes; }

    /**
     * @param notes la liste des notes à définir
     */
    public void setNotes(List<Note> notes) { this.notes = notes; }
}
