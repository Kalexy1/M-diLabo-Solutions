package com.medilabo.riskassessment.service;

import com.medilabo.riskassessment.dto.NoteDTO;
import com.medilabo.riskassessment.dto.PatientDTO;
import com.medilabo.riskassessment.dto.RiskAssessmentResponse;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

/**
 * Service métier du microservice Risk Assessment.
 * <p>
 * Cette classe contient la logique d’analyse du risque de diabète pour un patient,
 * en fonction de son âge, de son genre et des mots-clés présents dans ses notes médicales.
 * </p>
 */
@Service
public class RiskAssessmentService {

    private final RestTemplate restTemplate;

    /**
     * URL de l’API du microservice patient-service.
     */
    private final String PATIENT_API = "http://patient-service:8081/patients/";

    /**
     * URL de l’API du microservice note-service.
     */
    private final String NOTE_API = "http://note-service:8083/notes/patient/";

    /**
     * Liste des termes déclencheurs à rechercher dans les notes.
     */
    private final List<String> triggers = List.of(
        "Hémoglobine A1C", "Microalbumine", "Taille", "Poids",
        "Fumeur", "Fumeuse", "Anormal", "Cholestérol",
        "Vertiges", "Rechute", "Réaction", "Anticorps"
    );

    /**
     * Constructeur avec injection de {@link RestTemplate}.
     *
     * @param restTemplate client HTTP utilisé pour interroger les autres microservices
     */
    public RiskAssessmentService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Évalue le niveau de risque de diabète d’un patient et retourne uniquement le libellé.
     *
     * @param patientId identifiant du patient
     * @return le niveau de risque : "None", "Borderline", "In Danger", ou "Early onset"
     */
    public String assessRisk(Long patientId) {
        PatientDTO patient = restTemplate.getForObject(PATIENT_API + patientId, PatientDTO.class);
        NoteDTO[] notes = restTemplate.getForObject(NOTE_API + patientId, NoteDTO[].class);

        int age = calculateAge(patient.getDateNaissance());
        String genre = patient.getGenre();
        int triggerCount = countTriggerTerms(notes);

        return determineRiskLevel(age, genre, triggerCount);
    }

    /**
     * Calcule l'âge d'un patient à partir de sa date de naissance.
     *
     * @param birthDate la date de naissance
     * @return l’âge du patient en années
     */
    private int calculateAge(LocalDate birthDate) {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    /**
     * Compte le nombre de termes déclencheurs trouvés dans toutes les notes du patient.
     *
     * @param notes tableau de notes médicales
     * @return le nombre total de termes déclencheurs détectés
     */
    private int countTriggerTerms(NoteDTO[] notes) {
        int count = 0;
        if (notes != null) {
            for (NoteDTO note : notes) {
                String contenu = note.getContenu();
                if (contenu != null) {
                    String normalized = contenu.toLowerCase();
                    for (String trigger : triggers) {
                        if (normalized.contains(trigger.toLowerCase())) {
                            count++;
                        }
                    }
                }
            }
        }
        return count;
    }

    /**
     * Détermine le niveau de risque selon les règles métier fournies.
     *
     * @param age âge du patient
     * @param genre genre du patient ("M" ou "F")
     * @param triggerCount nombre de termes déclencheurs détectés
     * @return le niveau de risque : "None", "Borderline", "In Danger", ou "Early onset"
     */
    private String determineRiskLevel(int age, String genre, int triggerCount) {
        if (triggerCount == 0) return "None";

        if (age > 30) {
            if (triggerCount >= 8) return "Early onset";
            if (triggerCount >= 6) return "In Danger";
            if (triggerCount >= 2) return "Borderline";
        } else {
            if ("M".equalsIgnoreCase(genre)) {
                if (triggerCount >= 5) return "Early onset";
                if (triggerCount >= 3) return "In Danger";
            } else if ("F".equalsIgnoreCase(genre)) {
                if (triggerCount >= 7) return "Early onset";
                if (triggerCount >= 4) return "In Danger";
            }
        }
        return "None";
    }

    /**
     * Évalue le risque de diabète d’un patient et retourne une réponse détaillée incluant
     * ses informations personnelles, son âge et son niveau de risque.
     *
     * @param patientId identifiant du patient
     * @return un objet {@link RiskAssessmentResponse} complet
     */
    public RiskAssessmentResponse assessRiskDetailed(Long patientId) {
        PatientDTO patient = restTemplate.getForObject(PATIENT_API + patientId, PatientDTO.class);
        NoteDTO[] notes = restTemplate.getForObject(NOTE_API + patientId, NoteDTO[].class);

        int age = calculateAge(patient.getDateNaissance());
        String genre = patient.getGenre();
        int triggerCount = countTriggerTerms(notes);
        String risk = determineRiskLevel(age, genre, triggerCount);

        return new RiskAssessmentResponse(
            patient.getId(),
            patient.getPrenom(),
            patient.getNom(),
            age,
            risk
        );
    }
}
