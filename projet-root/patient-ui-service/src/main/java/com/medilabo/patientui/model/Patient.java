package com.medilabo.patientui.model;

import java.time.LocalDate;

/**
 * Représente un patient dans l’interface utilisateur.
 * <p>
 * Ce DTO (Data Transfer Object) est utilisé par le microservice
 * <strong>patient-ui-service</strong> pour communiquer avec le
 * <strong>patient-service</strong> via la Gateway. Il reflète la structure
 * du modèle de données du service patient.
 * </p>
 */
public class Patient {

    /**
     * Identifiant unique du patient.
     */
    private Long id;

    /**
     * Prénom du patient.
     */
    private String firstName;

    /**
     * Nom de famille du patient.
     */
    private String lastName;

    /**
     * Date de naissance du patient.
     */
    private LocalDate birthDate;

    /**
     * Genre du patient (ex. : "M", "F", ou autre).
     */
    private String gender;

    /**
     * Adresse postale du patient (optionnelle).
     */
    private String address;

    /**
     * Numéro de téléphone du patient (optionnel).
     */
    private String phone;

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
