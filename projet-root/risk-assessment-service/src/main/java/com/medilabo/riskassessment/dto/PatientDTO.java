package com.medilabo.riskassessment.dto;

import java.time.LocalDate;

/**
 * Représente un patient tel qu’il est reçu depuis le microservice
 * <strong>patient-service</strong>.
 * <p>
 * Ce DTO est utilisé par le microservice <strong>risk-assessment-service</strong>
 * pour calculer le niveau de risque de diabète à partir des informations
 * personnelles du patient.
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
    private String firstName;

    /**
     * Nom de famille du patient.
     */
    private String lastName;

    /**
     * Sexe du patient (ex. : "M" ou "F").
     */
    private String gender;

    /**
     * Date de naissance du patient.
     */
    private LocalDate birthDate;

    /**
     * Constructeur par défaut (nécessaire à la désérialisation JSON).
     */
    public PatientDTO() {}

    /**
     * Constructeur complet du DTO patient.
     *
     * @param id         identifiant du patient
     * @param firstName  prénom du patient
     * @param lastName   nom du patient
     * @param birthDate  date de naissance du patient
     * @param gender     sexe du patient
     */
    public PatientDTO(Long id, String firstName, String lastName, LocalDate birthDate, String gender) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.gender = gender;
    }

    /**
     * Retourne l’identifiant du patient.
     *
     * @return identifiant du patient
     */
    public Long getId() {
        return id;
    }

    /**
     * Définit l’identifiant du patient.
     *
     * @param id identifiant du patient
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retourne le prénom du patient.
     *
     * @return prénom du patient
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Définit le prénom du patient.
     *
     * @param firstName prénom du patient
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Retourne le nom de famille du patient.
     *
     * @return nom du patient
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Définit le nom de famille du patient.
     *
     * @param lastName nom du patient
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Retourne le sexe du patient.
     *
     * @return sexe du patient
     */
    public String getGender() {
        return gender;
    }

    /**
     * Définit le sexe du patient.
     *
     * @param gender sexe du patient
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * Retourne la date de naissance du patient.
     *
     * @return date de naissance du patient
     */
    public LocalDate getBirthDate() {
        return birthDate;
    }

    /**
     * Définit la date de naissance du patient.
     *
     * @param birthDate date de naissance du patient
     */
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }
}
