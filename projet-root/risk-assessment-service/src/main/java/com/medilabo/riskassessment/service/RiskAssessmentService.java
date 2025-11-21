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

/**
 * Service applicatif chargé d’évaluer le risque de diabète d’un patient.
 *
 * <p>
 * Il interroge les microservices externes (patients et notes), calcule le
 * nombre de termes déclencheurs présents dans les notes, détermine l’âge
 * du patient puis applique les règles métier pour produire un niveau de risque.
 * </p>
 */
@Service
public class RiskAssessmentService {

    /**
     * Client HTTP utilisé pour communiquer avec les microservices externes.
     */
    private final RestTemplate restTemplate;

    /**
     * URL de base de l’API des patients (sans identifiant à la fin).
     * Exemple : {@code http://patient-service:8081/api/patients}
     */
    private final String patientApiBase;

    /**
     * URL de base de l’API des notes.
     * Exemple : {@code http://note-service:8082/api/notes}
     */
    private final String noteApiBase;

    /**
     * Liste des termes déclencheurs recherchés dans le contenu des notes.
     */
    private static final List<String> TRIGGERS = List.of(
        "hémoglobine a1c", "microalbumine", "taille", "poids",
        "fumeur", "fumeuse", "anormal", "cholestérol",
        "vertiges", "rechute", "réaction", "anticorps"
    );

    /**
     * Construit le service d’évaluation du risque.
     *
     * @param restTemplate   client HTTP utilisé pour les appels sortants
     * @param patientApiBase URL de base de l’API patient (sans identifiant)
     * @param noteApiBase    URL de base de l’API des notes
     */
    public RiskAssessmentService(
            RestTemplate restTemplate,
            @Value("${patients.api.url:http://localhost:8081/api/patients}") String patientApiBase,
            @Value("${notes.api.url:http://localhost:8082/api/notes}") String noteApiBase) {

        this.restTemplate = restTemplate;
        this.patientApiBase = ensureEndsWithSlash(trimEnd(patientApiBase));
        this.noteApiBase = ensureEndsWithSlash(trimEnd(noteApiBase));
    }

    /**
     * Supprime un slash final éventuel de l’URL fournie.
     *
     * @param s chaîne représentant une URL
     * @return même URL sans slash final, ou chaîne vide si {@code null} ou blanche
     */
    private static String trimEnd(String s) {
        if (s == null || s.isBlank()) return "";
        return s.endsWith("/") ? s.substring(0, s.length() - 1) : s;
    }

    /**
     * S’assure qu’une URL se termine par un slash.
     *
     * @param s chaîne représentant une URL
     * @return URL terminée par {@code /}
     */
    private static String ensureEndsWithSlash(String s) {
        if (s == null || s.isBlank()) return "/";
        return s.endsWith("/") ? s : s + "/";
    }

    /**
     * Calcule le niveau de risque de diabète d’un patient et renvoie
     * uniquement le libellé du risque.
     *
     * @param patientId identifiant du patient
     * @return niveau de risque (par exemple {@code None}, {@code Borderline},
     *         {@code In Danger}, {@code Early onset})
     */
    public String assessRisk(Long patientId) {
        PatientDTO patient = restTemplate.getForObject(
                patientApiBase + patientId,
                PatientDTO.class
        );

        NoteDTO[] notes = restTemplate.getForObject(
                noteApiBase + "patient/" + patientId,
                NoteDTO[].class
        );

        if (patient == null) return "None";

        int age = calculateAge(patient.getBirthDate());
        String gender = patient.getGender();
        int triggerCount = countTriggerTerms(notes);

        return determineRiskLevel(age, gender, triggerCount);
    }

    /**
     * Calcule le niveau de risque de diabète d’un patient et renvoie
     * un objet détaillé contenant les informations patient et le risque.
     *
     * @param patientId identifiant du patient
     * @return un {@link RiskAssessmentResponse} détaillant le résultat
     */
    public RiskAssessmentResponse assessRiskDetailed(Long patientId) {
        PatientDTO patient = restTemplate.getForObject(
                patientApiBase + patientId,
                PatientDTO.class
        );

        NoteDTO[] notes = restTemplate.getForObject(
                noteApiBase + "patient/" + patientId,
                NoteDTO[].class
        );

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

    /**
     * Calcule l’âge à partir de la date de naissance.
     *
     * @param birthDate date de naissance
     * @return âge en années, ou {@code 0} si la date est nulle
     */
    private int calculateAge(LocalDate birthDate) {
        if (birthDate == null) return 0;
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    /**
     * Compte le nombre d’occurrences de termes déclencheurs
     * dans l’ensemble des notes fournies.
     *
     * @param notes tableau de notes à analyser
     * @return nombre total de déclencheurs trouvés
     */
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

    /**
     * Détermine le niveau de risque à partir de l’âge, du sexe et
     * du nombre de déclencheurs trouvés.
     *
     * @param age âge du patient
     * @param gender sexe du patient (ex. {@code "M"} ou {@code "F"})
     * @param triggerCount nombre de déclencheurs détectés
     * @return libellé du niveau de risque
     */
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
