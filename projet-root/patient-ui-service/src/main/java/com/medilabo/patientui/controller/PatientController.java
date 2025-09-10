package com.medilabo.patientui.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.medilabo.patientui.dto.RiskAssessmentResponse;
import com.medilabo.patientui.model.Patient;
import com.medilabo.patientui.service.NoteService;
import com.medilabo.patientui.service.PatientService;

import jakarta.servlet.http.HttpServletResponse;

/**
 * Contrôleur Spring MVC gérant l'interface utilisateur pour les patients.
 * <p>
 * Fournit des opérations CRUD, affichage des notes, évaluation des risques et génération de rapports PDF.
 */
@Controller
public class PatientController {

    private final PatientService patientService;
    private final NoteService noteService;
    private final RestTemplate restTemplate;

    /**
     * Constructeur avec injection des dépendances.
     *
     * @param patientService le service de gestion des patients
     * @param noteService le service de gestion des notes␊
     * @param restTemplate pour communiquer avec les microservices␊
     */
    public PatientController(PatientService patientService, NoteService noteService, RestTemplate restTemplate) {
        this.patientService = patientService;
        this.noteService = noteService;
        this.restTemplate = restTemplate;
    }

    /**
     * Affiche la liste de tous les patients avec leurs notes et niveaux de risque.
     *
     * @param model le modèle pour la vue Thymeleaf
     * @return le nom de la vue "patients"
     */
    @GetMapping("/patients")
    public String getAllPatients(Model model) {
    	List<Patient> patients = patientService.getAllPatients();
        Map<Long, List<Map<String, Object>>> notesByPatient = new HashMap<>();
        Map<Long, String> riskByPatient = new HashMap<>();

        for (Patient p : patients) {
            List<Map<String, Object>> notes = noteService.getNotesByPatientId(p.getId());
            notesByPatient.put(p.getId(), notes);

            try {
                RiskAssessmentResponse risk = restTemplate.getForObject(
                        "http://gateway-service:8080/assess/" + p.getId(),
                        RiskAssessmentResponse.class
                    );
                riskByPatient.put(p.getId(), risk.getRiskLevel());
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
     * @param model le modèle pour la vue
     * @return le nom de la vue "add-patient"
     */
    @GetMapping("/patients/new")
    public String showAddPatientForm(Model model) {
        model.addAttribute("patient", new Patient());
        return "add-patient";
    }

    /**
     * Crée un nouveau patient.
     *
     * @param patient le patient à enregistrer
     * @return redirection vers la liste des patients
     */
    @PostMapping("/patients")
    public String createPatient(@ModelAttribute Patient patient) {
    	patientService.createPatient(patient);
        return "redirect:/patients";
    }

    /**
     * Ajoute une note pour un patient donné.
     *
     * @param patientId l'identifiant du patient
     * @param contenu le contenu de la note
     * @return redirection vers la liste des patients
     */
    @PostMapping("/notes")
    public String ajouterNote(@RequestParam("patientId") Long patientId,
                              @RequestParam("contenu") String contenu) {
        noteService.ajouterNote(patientId, contenu);
        return "redirect:/patients";
    }

    /**
     * Affiche le formulaire de modification d’un patient existant.
     *
     * @param id l’identifiant du patient à modifier
     * @param model le modèle pour la vue
     * @return le nom de la vue "edit-patient"
     */
    @GetMapping("/patients/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
    	Patient patient = patientService.getPatientById(id)
            .orElseThrow(() -> new IllegalArgumentException("Patient non trouvé : " + id));
        model.addAttribute("patient", patient);
        return "edit-patient";
    }

    /**
     * Met à jour les informations d’un patient.
     *
     * @param patient le patient modifié
     * @return redirection vers la liste des patients
     */
    @PostMapping("/patients/update")
    public String updatePatient(@ModelAttribute Patient patient) {
    	patientService.updatePatient(patient.getId(), patient);
        return "redirect:/patients";
    }

    /**
     * Affiche les notes d’un patient donné.
     *
     * @param id l’identifiant du patient
     * @param model le modèle pour la vue
     * @return le nom de la vue "patient-notes"
     */
    @GetMapping("/patients/{id}/notes")
    public String showPatientNotes(@PathVariable Long id, Model model) {
    	Patient patient = patientService.getPatientById(id)
            .orElseThrow(() -> new IllegalArgumentException("Patient non trouvé : " + id));

        List<Map<String, Object>> notes = noteService.getNotesByPatientId(id);

        model.addAttribute("patient", patient);
        model.addAttribute("notes", notes);

        return "patient-notes";
    }

    /**
     * Affiche le rapport de risque pour un patient.
     *
     * @param id l’identifiant du patient
     * @param model le modèle pour la vue
     * @return le nom de la vue "risk-report"
     */
    @GetMapping("/patients/{id}/risk")
    public String showRiskReport(@PathVariable Long id, Model model) {
    	Patient patient = patientService.getPatientById(id)
            .orElseThrow(() -> new IllegalArgumentException("Patient non trouvé : " + id));

        RiskAssessmentResponse risk = restTemplate.getForObject(
                "http://gateway-service:8080/assess/" + id,
                RiskAssessmentResponse.class
            );

        model.addAttribute("patient", patient);
        model.addAttribute("risk", risk);
        return "risk-report";
    }

    /**
     * Supprime un patient.
     *
     * @param id l’identifiant du patient à supprimer
     * @return redirection vers la liste des patients
     */
    @GetMapping("/patients/delete/{id}")
    public String deletePatient(@PathVariable Long id) {
    	patientService.deletePatient(id);
        return "redirect:/patients";
    }

    /**
     * Génère un rapport PDF du niveau de risque pour un patient donné.
     *
     * @param id l’identifiant du patient
     * @param response la réponse HTTP pour l’écriture du fichier PDF
     * @throws IOException en cas d'erreur d'écriture
     * @throws DocumentException en cas d'erreur de génération du PDF
     */
    @GetMapping("/patients/{id}/report/pdf")
    public void downloadPdfReport(@PathVariable Long id, HttpServletResponse response) throws IOException, DocumentException {
    	Patient patient = patientService.getPatientById(id)
            .orElseThrow(() -> new IllegalArgumentException("Patient non trouvé : " + id));

        RiskAssessmentResponse risk = restTemplate.getForObject(
                "http://gateway-service:8080/assess/" + id,
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
        document.add(new Paragraph("Âge : " + risk.getAge()));
        document.add(new Paragraph("Risque détecté : " + risk.getRiskLevel()));
        document.close();
    }

}
