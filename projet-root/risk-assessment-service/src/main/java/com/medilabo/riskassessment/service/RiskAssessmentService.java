package com.medilabo.riskassessment.service;

import com.medilabo.riskassessment.dto.NoteDTO;
import com.medilabo.riskassessment.dto.PatientDTO;
import com.medilabo.riskassessment.dto.RiskAssessmentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
public class RiskAssessmentService {

    private final RestTemplate restTemplate;

    // Bases d’URL exposées par la Gateway
    // Exemple :
    //   PATIENT_API_BASE_URL = http://gateway-service:8080/api/patients
    //   NOTE_API_BASE_URL    = http://gateway-service:8080/api/notes/patient
    private final String patientApiBase;
    private final String noteApiBase;

    // Termes déclencheurs
    private static final List<String> TRIGGERS = List.of(
        "hémoglobine a1c", "microalbumine", "taille", "poids",
        "fumeur", "fumeuse", "anormal", "cholestérol",
        "vertiges", "rechute", "réaction", "anticorps"
    );

    public RiskAssessmentService(
            RestTemplate restTemplate,
            @Value("${PATIENT_API_BASE_URL:http://gateway-service:8080/api/patients}") String patientApiBase,
            @Value("${NOTE_API_BASE_URL:http://gateway-service:8080/api/notes/patient}") String noteApiBase) {
        this.restTemplate = restTemplate;
        this.patientApiBase = ensureEndsWithSlash(trimEnd(patientApiBase));
        this.noteApiBase = ensureEndsWithSlash(trimEnd(noteApiBase));
    }

    /** Supprime un '/' final s'il existe (ne renvoie jamais null). */
    private static String trimEnd(String s) {
        if (s == null || s.isBlank()) return "";
        return s.endsWith("/") ? s.substring(0, s.length() - 1) : s;
    }

    /** Ajoute un '/' final si absent. */
    private static String ensureEndsWithSlash(String s) {
        if (s == null || s.isBlank()) return "/";
        return s.endsWith("/") ? s : s + "/";
    }

    // -------------------------------------------------------------------------
    //  Méthodes principales
    // -------------------------------------------------------------------------

    /** Retourne le niveau de risque : "None", "Borderline", "In Danger", "Early onset". */
    public String assessRisk(Long patientId) {
        PatientDTO patient = restTemplate.getForObject(patientApiBase + patientId, PatientDTO.class);
        NoteDTO[] notes = restTemplate.getForObject(noteApiBase + patientId, NoteDTO[].class);

        if (patient == null) return "None"; // sécurité si appel externe échoue

        int age = calculateAge(patient.getBirthDate());
        String gender = patient.getGender();
        int triggerCount = countTriggerTerms(notes);

        return determineRiskLevel(age, gender, triggerCount);
    }

    /** Variante détaillée : renvoie toutes les infos dans un objet de réponse. */
    public RiskAssessmentResponse assessRiskDetailed(Long patientId) {
        PatientDTO patient = restTemplate.getForObject(patientApiBase + patientId, PatientDTO.class);
        NoteDTO[] notes = restTemplate.getForObject(noteApiBase + patientId, NoteDTO[].class);

        if (patient == null) {
            return new RiskAssessmentResponse(null, null, null, 0, "None");
        }

        int age = calculateAge(patient.getBirthDate());
        String gender = patient.getGender();
        int triggerCount = countTriggerTerms(notes);
        String risk = determineRiskLevel(age, gender, triggerCount);

        return new RiskAssessmentResponse(
            patient.getId(),
            patient.getFirstName(),
            patient.getLastName(),
            age,
            risk
        );
    }

    // -------------------------------------------------------------------------
    //  Méthodes utilitaires
    // -------------------------------------------------------------------------

    private int calculateAge(LocalDate birthDate) {
        if (birthDate == null) return 0;
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    private int countTriggerTerms(NoteDTO[] notes) {
        if (notes == null) return 0;
        int count = 0;
        for (NoteDTO n : notes) {
            if (n == null) continue;
            String c = n.getContent();
            if (c == null || c.isBlank()) continue;
            String lower = c.toLowerCase();
            for (String t : TRIGGERS) {
                if (lower.contains(t)) count++;
            }
        }
        return count;
    }

    private String determineRiskLevel(int age, String gender, int triggerCount) {
        if (triggerCount == 0) return "None";

        if (age > 30) {
            if (triggerCount >= 8) return "Early onset";
            if (triggerCount >= 6) return "In Danger";
            if (triggerCount >= 2) return "Borderline";
        } else {
            if ("M".equalsIgnoreCase(gender)) {
                if (triggerCount >= 5) return "Early onset";
                if (triggerCount >= 3) return "In Danger";
            } else if ("F".equalsIgnoreCase(gender)) {
                if (triggerCount >= 7) return "Early onset";
                if (triggerCount >= 4) return "In Danger";
            }
        }
        return "None";
    }
}
