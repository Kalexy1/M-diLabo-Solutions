package com.medilabo.patientservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;

/**
 * Représente un patient au sein du système patient-service.
 *
 * <p>Cette entité correspond à la table {@code patients} et contient les
 * informations personnelles de base d’un patient.</p>
 */
@Entity
@Table(name = "patients")
public class Patient {

    /**
     * Identifiant unique du patient (clé primaire).
     * Généré automatiquement par la base de données.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Prénom du patient.
     * Champ obligatoire.
     */
    @NotBlank
    @Column(nullable = false)
    private String firstName;

    /**
     * Nom de famille du patient.
     * Champ obligatoire.
     */
    @NotBlank
    @Column(nullable = false)
    private String lastName;

    /**
     * Date de naissance du patient.
     * Doit correspondre à une date passée.
     */
    @NotNull
    @Past
    @Column(nullable = false)
    private LocalDate birthDate;

    /**
     * Genre du patient (ex. : "M", "F", autre).
     * Champ obligatoire.
     */
    @NotBlank
    @Column(nullable = false)
    private String gender;

    /**
     * Adresse postale du patient (facultative).
     */
    private String address;

    /**
     * Numéro de téléphone du patient (facultatif).
     */
    private String phone;

    /**
     * Constructeur complet.
     *
     * @param firstName prénom du patient
     * @param lastName nom du patient
     * @param birthDate date de naissance
     * @param gender genre du patient
     * @param address adresse postale
     * @param phone numéro de téléphone
     */
    public Patient(String firstName, String lastName, LocalDate birthDate, String gender, String address, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.gender = gender;
        this.address = address;
        this.phone = phone;
    }

    /**
     * Constructeur sans argument requis par JPA.
     */
    public Patient() {}

    // ----- GETTERS & SETTERS -----

    /** @return identifiant unique du patient */
    public Long getId() {
        return id;
    }

    /** @param id identifiant du patient */
    public void setId(Long id) {
        this.id = id;
    }

    /** @return prénom du patient */
    public String getFirstName() {
        return firstName;
    }

    /** @param firstName prénom à définir */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /** @return nom du patient */
    public String getLastName() {
        return lastName;
    }

    /** @param lastName nom à définir */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /** @return date de naissance du patient */
    public LocalDate getBirthDate() {
        return birthDate;
    }

    /** @param birthDate date de naissance à définir */
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    /** @return genre du patient */
    public String getGender() {
        return gender;
    }

    /** @param gender genre à définir */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /** @return adresse du patient */
    public String getAddress() {
        return address;
    }

    /** @param address adresse à définir */
    public void setAddress(String address) {
        this.address = address;
    }

    /** @return numéro de téléphone du patient */
    public String getPhone() {
        return phone;
    }

    /** @param phone numéro de téléphone à définir */
    public void setPhone(String phone) {
        this.phone = phone;
    }
}
