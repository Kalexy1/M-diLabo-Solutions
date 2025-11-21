package com.medilabo.patientui.model;

import java.time.LocalDate;

/**
 * Représente un patient dans l’interface utilisateur.
 *
 * <p>Ce DTO (Data Transfer Object) est utilisé par le microservice
 * <strong>patient-ui-service</strong> pour consommer les données exposées
 * par le <strong>patient-service</strong> via la Gateway.</p>
 *
 * <p>Il reflète la structure du modèle de données du service patient, afin
 * d'assurer une sérialisation/désérialisation correcte lors des appels REST.</p>
 */
public class Patient {

    /** Identifiant unique du patient. */
    private Long id;

    /** Prénom du patient. */
    private String firstName;

    /** Nom de famille du patient. */
    private String lastName;

    /** Date de naissance du patient. */
    private LocalDate birthDate;

    /** Genre du patient (ex. "M", "F" ou autre). */
    private String gender;

    /** Adresse postale du patient (optionnelle). */
    private String address;

    /** Numéro de téléphone du patient (optionnel). */
    private String phone;

    /** @return identifiant unique du patient */
    public Long getId() {
        return id;
    }

    /** @param id identifiant unique du patient */
    public void setId(Long id) {
        this.id = id;
    }

    /** @return prénom du patient */
    public String getFirstName() {
        return firstName;
    }

    /** @param firstName prénom du patient */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /** @return nom de famille du patient */
    public String getLastName() {
        return lastName;
    }

    /** @param lastName nom de famille du patient */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /** @return date de naissance du patient */
    public LocalDate getBirthDate() {
        return birthDate;
    }

    /** @param birthDate date de naissance du patient */
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    /** @return genre du patient */
    public String getGender() {
        return gender;
    }

    /** @param gender genre du patient */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /** @return adresse postale du patient */
    public String getAddress() {
        return address;
    }

    /** @param address adresse postale du patient */
    public void setAddress(String address) {
        this.address = address;
    }

    /** @return numéro de téléphone du patient */
    public String getPhone() {
        return phone;
    }

    /** @param phone numéro de téléphone du patient */
    public void setPhone(String phone) {
        this.phone = phone;
    }
}
