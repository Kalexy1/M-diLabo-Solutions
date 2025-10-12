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
 * <p>
 * Ce service interroge les microservices externes (patients et notes) via la Gateway,
 * calcule le nombre de déclencheurs présents dans les notes, détermine l’âge du patient
 * puis applique les règles métier pour produire un niveau de risque.
 * </p>
 */
@Service
public class RiskAssessmentService {

    /**
     * Client HTTP utilisé pour communiquer avec les microservices via la Gateway.
     */
    private final RestTemplate restTemplate;

    /**
     * Base URL (terminée par un {@code /}) vers l’API des patients exposée par la Gateway.
     * <p>Exemple : {@code http://gateway-service:8080/api/patients/}</p>
     */
    private final String patientApiBase;

    /**
     * Base URL (terminée par un {@code /}) vers l’API des notes exposée par la Gateway.
     * <p>Exemple : {@code http://gateway-service:8080/api/notes/patient/}</p>
     */
    private final String noteApiBase;

    /**
     * Ensemble des termes déclencheurs recherchés dans le contenu des notes.
     */
    private static final List<String> TRIGGERS = List.of(
        "hémoglobine a1c", "microalbumine", "taille", "poids",
        "fumeur", "fumeuse", "anormal", "cholestérol",
        "vertiges", "rechute", "réaction", "anticorps"
    );

    /**
     * Construit le service d’évaluation du risque.
     *
     * @param restTemplate    client HTTP pour les appels sortants
     * @param patientApiBase  base URL de l’API des patients (peut ne pas se terminer par {@code /})
     * @param noteApiBase     base URL de l’API des notes (peut ne pas se terminer par {@code /})
     */
    public RiskAssessmentService(
            RestTemplate restTemplate,
            @Value("${PATIENT_API_BASE_URL:http://gateway-service:8080/api/patients}") String patientApiBase,
            @Value("${NOTE_API_BASE_URL:http://gateway-service:8080/api/notes/patient}") String noteApiBase) {
        this.restTemplate = restTemplate;
        this.patientApiBase = ensureEndsWithSlash(trimEnd(patientApiBase));
        this.noteApiBase = ensureEndsWithSlash(trimEnd(noteApiBase));
    }

    /**
     * Supprime un {@code /} final s’il existe.
     *
     * @param s chaîne à normaliser (peut être nulle)
     * @return la chaîne sans {@code /} final ; jamais {@code null}
     */
    private static String trimEnd(String s) {
        if (s == null || s.isBlank()) return "";
        return s.endsWith("/") ? s.substring(0, s.length() - 1) : s;
    }

    /**
     * Ajoute un {@code /} final s’il est absent.
     *
     * @param s chaîne à normaliser (peut être nulle)
     * @return la chaîne terminée par {@code /}
     */
    private static String ensureEndsWithSlash(String s) {
        if (s == null || s.isBlank()) return "/";
        return s.endsWith("/") ? s : s + "/";
    }

    /**
     * Calcule le niveau de risque sous forme textuelle.
     * <p>
     * Valeurs possibles : {@code "None"}, {@code "Borderline"}, {@code "In Danger"}, {@code "Early onset"}.
     * Si le patient n’est pas récupéré (appel externe en échec), retourne {@code "None"}.
     * </p>
     *
     * @param patientId identifiant du patient
     * @return le niveau de risque évalué
     */
    public String assessRisk(Long patientId) {
        PatientDTO patient = restTemplate.getForObject(patientApiBase + patientId, PatientDTO.class);
        NoteDTO[] notes = restTemplate.getForObject(noteApiBase + patientId, NoteDTO[].class);

        if (patient == null) return "None";

        int age = calculateAge(patient.getBirthDate());
        String gender = patient.getGender();
        int triggerCount = countTriggerTerms(notes);

        return determineRiskLevel(age, gender, triggerCount);
    }

    /**
     * Variante détaillée retournant un objet de réponse complet.
     * <p>
     * Si le patient n’est pas récupéré, retourne une réponse avec valeurs par défaut
     * et un niveau de risque {@code "None"}.
     * </p>
     *
     * @param patientId identifiant du patient
     * @return une réponse détaillée d’évaluation de risque
     */
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

    /**
     * Calcule l’âge à partir de la date de naissance.
     *
     * @param birthDate date de naissance
     * @return l’âge en années ; {@code 0} si la date est nulle
     */
    private int calculateAge(LocalDate birthDate) {
        if (birthDate == null) return 0;
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    /**
     * Compte le nombre de termes déclencheurs présents dans les notes.
     * <p>
     * La recherche est effectuée en insensible à la casse sur la liste {@link #TRIGGERS}.
     * </p>
     *
     * @param notes tableau de notes (peut être {@code null})
     * @return le nombre total de déclencheurs trouvés
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
     * Détermine le niveau de risque en fonction de l’âge, du genre et du nombre de déclencheurs.
     * <p>
     * Règles métier :
     * <ul>
     *     <li>Si aucun déclencheur : {@code "None"}.</li>
     *     <li>Âge &gt; 30 :
     *         <ul>
     *             <li>&ge; 8 ⇒ {@code "Early onset"}</li>
     *             <li>&ge; 6 ⇒ {@code "In Danger"}</li>
     *             <li>&ge; 2 ⇒ {@code "Borderline"}</li>
     *         </ul>
     *     </li>
     *     <li>Âge ≤ 30 :
     *         <ul>
     *             <li>Homme (M) : &ge; 5 ⇒ {@code "Early onset"}, &ge; 3 ⇒ {@code "In Danger"}</li>
     *             <li>Femme (F) : &ge; 7 ⇒ {@code "Early onset"}, &ge; 4 ⇒ {@code "In Danger"}</li>
     *         </ul>
     *     </li>
     * </ul>
     * Dans les autres cas, retourne {@code "None"}.
     * </p>
     *
     * @param age          âge du patient
     * @param gender       genre du patient (ex. {@code "M"} ou {@code "F"})
     * @param triggerCount nombre de déclencheurs détectés
     * @return le niveau de risque évalué
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
