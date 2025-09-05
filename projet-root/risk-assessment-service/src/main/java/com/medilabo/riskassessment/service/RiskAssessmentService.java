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
 * Application: com.medilabo.riskassessment.service
 * <p>
 * Classe <strong>RiskAssessmentService</strong>.
 * <br/>
 * Rôle : Porte la logique métier d’évaluation du risque de diabète pour un patient.
 * </p>
 * <p>
 * Le calcul du risque s’appuie sur :
 * <ul>
 *   <li>les données démographiques du patient (âge, genre) issues de <em>patient-service</em>,</li>
 *   <li>la présence de <em>termes déclencheurs</em> dans ses notes issues de <em>note-service</em>.</li>
 * </ul>
 * Les appels inter-services transitent par la <strong>Gateway</strong> via un {@link RestTemplate}.
 * </p>
 */
@Service
public class RiskAssessmentService {

    private final RestTemplate restTemplate;

    /**
     * URL de l’API <em>patient-service</em> exposée via la Gateway.
     * <p>Format attendu&nbsp;: {@code http://gateway-service:8080/patients/{id}}</p>
     */
    private final String PATIENT_API = "http://gateway-service:8080/patients/";

    /**
     * URL de l’API <em>note-service</em> exposée via la Gateway.
     * <p>Format attendu&nbsp;: {@code http://gateway-service:8080/notes/patient/{id}}</p>
     */
    private final String NOTE_API = "http://gateway-service:8080/notes/patient/";

    /**
     * Liste des termes déclencheurs à rechercher dans les notes.
     * <p>
     * La recherche est réalisée de façon <em>insensible à la casse</em> et
     * compte une occurrence par terme présent dans la note (pas de dédoublonnage intra-note).
     * </p>
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
     * <p>
     * Étapes&nbsp;:
     * <ol>
     *   <li>Récupération du patient ({@link PatientDTO}) via {@link #PATIENT_API}.</li>
     *   <li>Récupération des notes ({@link NoteDTO}[]) via {@link #NOTE_API}.</li>
     *   <li>Calcul de l’âge, comptage des déclencheurs, application des règles métier.</li>
     * </ol>
     * </p>
     *
     * @param patientId identifiant du patient
     * @return le niveau de risque parmi {@code "None"}, {@code "Borderline"},
     *         {@code "In Danger"} ou {@code "Early onset"}
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
     * Calcule l'âge en années complètes à partir de la date de naissance.
     *
     * @param birthDate date de naissance
     * @return âge du patient en années
     */
    private int calculateAge(LocalDate birthDate) {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    /**
     * Compte le nombre total de termes déclencheurs détectés dans l’ensemble des notes.
     * <p>
     * Règles&nbsp;:
     * <ul>
     *   <li>Ignoré si la liste des notes est nulle.</li>
     *   <li>Comparaison insensible à la casse ({@code toLowerCase}).</li>
     *   <li>Chaque terme de {@link #triggers} présent dans le contenu incrémente le compteur d’1.</li>
     * </ul>
     * </p>
     *
     * @param notes tableau de notes médicales (peut être {@code null})
     * @return nombre de déclencheurs détectés
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
     * Applique les règles métier pour dériver le niveau de risque à partir de l’âge,
     * du genre et du nombre de déclencheurs détectés.
     * <p>
     * Règles synthétiques&nbsp;:
     * <ul>
     *   <li>Si aucun déclencheur&nbsp;: {@code "None"}.</li>
     *   <li>Âge &gt; 30 ans&nbsp;:
     *     <ul>
     *       <li>≥ 8 → {@code "Early onset"}</li>
     *       <li>≥ 6 → {@code "In Danger"}</li>
     *       <li>≥ 2 → {@code "Borderline"}</li>
     *     </ul>
     *   </li>
     *   <li>Âge ≤ 30 ans&nbsp;:
     *     <ul>
     *       <li>Homme ({@code "M"})&nbsp;: ≥ 5 → {@code "Early onset"}, ≥ 3 → {@code "In Danger"}</li>
     *       <li>Femme ({@code "F"})&nbsp;: ≥ 7 → {@code "Early onset"}, ≥ 4 → {@code "In Danger"}</li>
     *     </ul>
     *   </li>
     * </ul>
     * </p>
     *
     * @param age          âge du patient
     * @param genre        genre du patient ({@code "M"} ou {@code "F"})
     * @param triggerCount nombre de déclencheurs
     * @return le niveau de risque : {@code "None"}, {@code "Borderline"},
     *         {@code "In Danger"} ou {@code "Early onset"}
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
     * Évalue le risque de diabète d’un patient et retourne une réponse détaillée.
     * <p>
     * La réponse inclut&nbsp;: l’identifiant, le prénom, le nom, l’âge calculé,
     * et le niveau de risque évalué.
     * </p>
     *
     * @param patientId identifiant du patient
     * @return un {@link RiskAssessmentResponse} complet
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
