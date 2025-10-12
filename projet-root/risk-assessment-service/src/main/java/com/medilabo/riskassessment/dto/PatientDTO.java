package com.medilabo.riskassessment.dto;

import java.time.LocalDate;

/**
 * DTO representing a Patient coming from the patient-service.
 * Used by risk-assessment-service to compute diabetes risk level.
 */
public class PatientDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String gender;
    private LocalDate birthDate;

    public PatientDTO() {}

    public PatientDTO(Long id, String firstName, String lastName, LocalDate birthDate, String gender) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.gender = gender;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
}
