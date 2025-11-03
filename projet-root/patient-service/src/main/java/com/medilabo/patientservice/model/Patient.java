package com.medilabo.patientservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;

/**
 * Représente un patient au sein du système.
 * <p>
 * Cette entité est mappée à la table {@code patients} de la base de données
 * et contient les informations personnelles et médicales de base d’un patient.
 * </p>
 */
@Entity
@Table(name = "patients")
public class Patient {

    /**
     * Identifiant unique du patient (clé primaire).
     * Généré automatiquement par la base de données (AUTO_INCREMENT MySQL).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Prénom du patient.
     * Ce champ est obligatoire.
     */
    @NotBlank
    @Column(nullable = false)
    private String firstName;

    /**
     * Nom de famille du patient.
     * Ce champ est obligatoire.
     */
    @NotBlank
    @Column(nullable = false)
    private String lastName;

    /**
     * Date de naissance du patient.
     * Doit être une date passée.
     */
    @NotNull
    @Past
    @Column(nullable = false)
    private LocalDate birthDate;

    /**
     * Genre du patient (ex. : "M", "F" ou autre).
     * Ce champ est obligatoire.
     */
    @NotBlank
    @Column(nullable = false)
    private String gender;

    /**
     * Adresse postale du patient (optionnelle).
     */
    private String address;

    /**
     * Numéro de téléphone du patient (optionnel).
     */
    private String phone;

    public Patient(String firstName, String lastName, LocalDate birthDate, String gender, String address, String phone) {
		this.firstName=firstName;
		this.lastName=lastName;
		this.birthDate=birthDate;
		this.gender=gender;
		this.address=address;
		this.phone=phone;
	}

	public Patient() {
		// TODO Auto-generated constructor stub
	}

	/**
     * Retourne l’identifiant unique du patient.
     *
     * @return l’identifiant du patient
     */
    public Long getId() {
        return id;
    }

    /**
     * Définit l’identifiant du patient.
     *
     * @param id l’identifiant à définir
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retourne le prénom du patient.
     *
     * @return le prénom du patient
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Définit le prénom du patient.
     *
     * @param firstName le prénom à définir
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Retourne le nom de famille du patient.
     *
     * @return le nom du patient
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Définit le nom de famille du patient.
     *
     * @param lastName le nom à définir
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Retourne la date de naissance du patient.
     *
     * @return la date de naissance
     */
    public LocalDate getBirthDate() {
        return birthDate;
    }

    /**
     * Définit la date de naissance du patient.
     *
     * @param birthDate la date de naissance à définir
     */
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    /**
     * Retourne le genre du patient.
     *
     * @return le genre du patient
     */
    public String getGender() {
        return gender;
    }

    /**
     * Définit le genre du patient.
     *
     * @param gender le genre à définir
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * Retourne l’adresse postale du patient.
     *
     * @return l’adresse du patient
     */
    public String getAddress() {
        return address;
    }

    /**
     * Définit l’adresse postale du patient.
     *
     * @param address l’adresse à définir
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Retourne le numéro de téléphone du patient.
     *
     * @return le numéro de téléphone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Définit le numéro de téléphone du patient.
     *
     * @param phone le numéro à définir
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }
}
