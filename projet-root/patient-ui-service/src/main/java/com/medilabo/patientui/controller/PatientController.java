package com.medilabo.patientui.controller;

import com.medilabo.patientui.dto.RiskAssessmentResponse;
import com.medilabo.patientui.model.Patient;
import com.medilabo.patientui.service.NoteService;
import com.medilabo.patientui.service.PatientService;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Application: com.medilabo.patientui.controller
 * <p>
 * Classe <strong>PatientController</strong>.
 * <br/>
 * Rôle : Gère les vues Thymeleaf de l’UI patient (liste, création, édition, notes, rapport PDF).
 * </p>
 * <p>
 * Sécurité & CSRF :
 * <ul>
 *   <li>Consultations en <b>GET</b> (liste, formulaires, notes, rapports) : aucun effet de bord.</li>
 *   <li>Actions d’écriture en <b>POST</b> (création, mise à jour, suppression, ajout de note) :
 *       protégées par <b>CSRF</b> via les formulaires.</li>
 *   <li>La suppression est exposée en <b>POST</b> pour respecter la sémantique HTTP et bénéficier de la protection CSRF.</li>
 * </ul>
 * </p>
 */
@Controller
public class PatientController {

    private final PatientService patientService;
    private final NoteService noteService;
    private final RestTemplate restTemplate;

    /** URL de base vers le gateway-service (appel des autres microservices via Gateway). */
    private static final String GATEWAY_BASE_URL = "http://gateway-service:8080";

    /**
     * Injection des dépendances.
     *
     * @param patientService service de gestion des patients (UI ↔ backend patient-service)
     * @param noteService    service d’accès aux notes (UI ↔ note-service)
     * @param restTemplate   client HTTP pour interroger le risk-service via la Gateway
     */
    public PatientController(PatientService patientService, NoteService noteService, RestTemplate restTemplate) {
        this.patientService = patientService;
        this.noteService = noteService;
        this.restTemplate = restTemplate;
    }

    /**
     * Affiche la liste des patients, enrichie de leurs notes et de leur niveau de risque.
     *
     * @param model modèle Thymeleaf
     * @return vue {@code patients}
     */
    @GetMapping("/patients")
    @PreAuthorize("hasAnyRole('ORGANISATEUR', 'PRATICIEN')")
    public String getAllPatients(Model model) {
        List<Patient> patients = patientService.findAll();
        Map<Long, List<Map<String, Object>>> notesByPatient = new HashMap<>();
        Map<Long, String> riskByPatient = new HashMap<>();

        for (Patient p : patients) {
            notesByPatient.put(p.getId(), noteService.getNotesByPatientId(p.getId()));
            try {
                RiskAssessmentResponse risk = restTemplate.getForObject(
                        GATEWAY_BASE_URL + "/risk/assess/" + p.getId(),
                        RiskAssessmentResponse.class
                );
                riskByPatient.put(p.getId(), risk != null ? risk.getRiskLevel() : "Non disponible");
            } catch (Exception e) {
                riskByPatient.put(p.getId(), "Non disponible");
            }
        }

        model.addAttribute("patients", patients);
        model.addAttribute("notesByPatient", notesByPatient);
        model.addAttribute("riskByPatient", riskByPatient);
        return "patients";
    }

    /**
     * Affiche le formulaire d’ajout d’un patient.
     *
     * @param model modèle Thymeleaf
     * @return vue {@code add-patient}
     */
    @GetMapping("/patients/new")
    @PreAuthorize("hasRole('ORGANISATEUR')")
    public String showAddPatientForm(Model model) {
        model.addAttribute("patient", new Patient());
        return "add-patient";
    }

    /**
     * Crée un nouveau patient (POST protégé par CSRF).
     *
     * @param patient patient à créer
     * @return redirection vers {@code /patients}
     */
    @PostMapping("/patients")
    @PreAuthorize("hasRole('ORGANISATEUR')")
    public String createPatient(@ModelAttribute Patient patient) {
        patientService.save(patient);
        return "redirect:/patients";
    }

    /**
     * Ajoute une note pour un patient (POST protégé par CSRF).
     *
     * @param patientId identifiant du patient
     * @param contenu   contenu de la note
     * @return redirection vers {@code /patients}
     */
    @PostMapping("/notes")
    @PreAuthorize("hasRole('PRATICIEN')")
    public String ajouterNote(@RequestParam("patientId") Long patientId,
                              @RequestParam("contenu") String contenu) {
        noteService.ajouterNote(patientId, contenu);
        return "redirect:/patients";
    }

    /**
     * Affiche le formulaire d’édition d’un patient.
     *
     * @param id    identifiant du patient
     * @param model modèle Thymeleaf
     * @return vue {@code edit-patient} ou redirection vers {@code /patients} si non trouvé
     */
    @GetMapping("/patients/edit/{id}")
    @PreAuthorize("hasRole('ORGANISATEUR')")
    public String showEditForm(@PathVariable Long id, Model model) {
        try {
            Patient patient = patientService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Patient non trouvé : " + id));
            model.addAttribute("patient", patient);
            return "edit-patient";
        } catch (IllegalArgumentException e) {
            return "redirect:/patients";
        }
    }

    /**
     * Met à jour les informations d’un patient (POST protégé par CSRF).
     *
     * @param patient patient mis à jour (l’ID doit être renseigné)
     * @return redirection vers {@code /patients}
     */
    @PostMapping("/patients/update")
    @PreAuthorize("hasRole('ORGANISATEUR')")
    public String updatePatient(@ModelAttribute Patient patient) {
        patientService.save(patient);
        return "redirect:/patients";
    }

    /**
     * Affiche l’historique des notes d’un patient.
     *
     * @param id    identifiant du patient
     * @param model modèle Thymeleaf
     * @return vue {@code patient-notes}
     */
    @GetMapping("/patients/{id}/notes")
    @PreAuthorize("hasRole('PRATICIEN')")
    public String showPatientNotes(@PathVariable Long id, Model model) {
        Patient patient = patientService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Patient non trouvé : " + id));
        List<Map<String, Object>> notes = noteService.getNotesByPatientId(id);
        model.addAttribute("patient", patient);
        model.addAttribute("notes", notes);
        return "patient-notes";
    }

    /**
     * Affiche le rapport de risque d’un patient.
     *
     * @param id    identifiant du patient
     * @param model modèle Thymeleaf
     * @return vue {@code risk-report}
     */
    @GetMapping("/patients/{id}/risk")
    @PreAuthorize("hasRole('PRATICIEN')")
    public String showRiskReport(@PathVariable Long id, Model model) {
        Patient patient = patientService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Patient non trouvé : " + id));
        RiskAssessmentResponse risk = restTemplate.getForObject(
                GATEWAY_BASE_URL + "/risk/assess/" + id,
                RiskAssessmentResponse.class
        );
        model.addAttribute("patient", patient);
        model.addAttribute("risk", risk);
        return "risk-report";
    }

    /**
     * Supprime un patient (POST protégé par CSRF).
     *
     * @param id identifiant du patient à supprimer
     * @return redirection vers {@code /patients}
     */
    @PostMapping("/patients/delete/{id}")
    @PreAuthorize("hasRole('ORGANISATEUR')")
    public String deletePatient(@PathVariable Long id) {
        patientService.deleteById(id);
        return "redirect:/patients";
    }

    /**
     * Génère et télécharge le rapport PDF du risque d’un patient.
     *
     * @param id       identifiant du patient
     * @param response réponse HTTP (flux sortant du PDF)
     * @throws IOException       en cas d’erreur d’écriture de la réponse
     * @throws DocumentException en cas d’erreur iText lors de la génération du PDF
     */
    @GetMapping("/patients/{id}/report/pdf")
    @PreAuthorize("hasRole('PRATICIEN')")
    public void downloadPdfReport(@PathVariable Long id, HttpServletResponse response)
            throws IOException, DocumentException {
        Patient patient = patientService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Patient non trouvé : " + id));
        RiskAssessmentResponse risk = restTemplate.getForObject(
                GATEWAY_BASE_URL + "/risk/assess/" + id,
                RiskAssessmentResponse.class
        );

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=rapport_patient_" + id + ".pdf");

        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();
        document.add(new Paragraph("Rapport de risque de diabète"));
        document.add(new Paragraph(" "));
        document.add(new Paragraph("Nom : " + patient.getNom()));
        document.add(new Paragraph("Prénom : " + patient.getPrenom()));
        document.add(new Paragraph("Date de naissance : " + patient.getDateNaissance()));
        document.add(new Paragraph("Genre : " + patient.getGenre()));
        if (risk != null) {
            document.add(new Paragraph("Âge : " + risk.getAge()));
            document.add(new Paragraph("Risque détecté : " + risk.getRiskLevel()));
        } else {
            document.add(new Paragraph("Risque détecté : Non disponible"));
        }
        document.close();
    }
}
