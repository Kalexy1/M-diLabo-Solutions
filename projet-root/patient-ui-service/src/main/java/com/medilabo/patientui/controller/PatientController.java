package com.medilabo.patientui.controller;

import com.medilabo.patientui.model.Note;
import com.medilabo.patientui.model.Patient;
import com.medilabo.patientui.model.RiskAssessmentResponse;
import com.medilabo.patientui.service.NoteService;
import com.medilabo.patientui.service.PatientService;
import com.medilabo.patientui.service.RiskService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur UI (patient-ui-service).
 *
 * IMPORTANT :
 * - Le Gateway expose /ui/** au navigateur.
 * - Il supprime /ui avant de proxyfier vers patient-ui.
 *   Donc ici, les mappings commencent par /patients (sans /ui).
 */
@Controller
public class PatientController {

    private final PatientService patients;
    private final NoteService notes;
    private final RiskService risk;

    public PatientController(PatientService patients, NoteService notes, RiskService risk) {
        this.patients = patients;
        this.notes = notes;
        this.risk = risk;
    }

    /**
     * Page d'accueil interne.
     * Appelée via /ui ou /ui/ côté navigateur, le Gateway enlève /ui et arrive ici sur "/" :
     * on renvoie directement la liste des patients, sans redirection HTTP.
     */
    @GetMapping({ "", "/" })
    public String home(Model model, HttpServletRequest request) {
        return listPatients(model, request);
    }

    /** Liste des patients (mapping interne : /patients). */
    @GetMapping("/patients")
    public String listPatients(Model model, HttpServletRequest request) {
        List<Patient> all = patients.findAll(request);
        model.addAttribute("patients", all);
        return "patients";
    }

    /** Formulaire d'ajout (template : add-patient.html). */
    @GetMapping("/patients/new")
    public String showAddForm(Model model) {
        model.addAttribute("patient", new Patient());
        return "add-patient";
    }

    /**
     * Création d'un nouveau patient.
     *
     * IMPORTANT : on ne fait plus de "redirect:/ui/patients" pour éviter
     * que le navigateur essaie d'appeler directement "patient-ui-service:8084".
     * On crée, puis on recharge la liste et on renvoie la vue "patients".
     */
    @PostMapping("/patients")
    public String createPatient(@ModelAttribute("patient") Patient payload,
                                HttpServletRequest request,
                                Model model) {

        patients.create(payload, request);

        // Recharge la liste et renvoie la même vue que listPatients()
        List<Patient> all = patients.findAll(request);
        model.addAttribute("patients", all);
        return "patients";
    }

    /** Formulaire d'édition (template : edit-patient.html). */
    @GetMapping("/patients/edit/{id}")
    public String showEditForm(@PathVariable Long id,
                               Model model,
                               HttpServletRequest request) {
        Patient patient = patients.getOne(id, request);
        model.addAttribute("patient", patient);
        return "edit-patient";
    }

    /**
     * Mise à jour d'un patient.
     *
     * Le template edit-patient.html envoie son formulaire vers /patients/update
     * avec un champ hidden "id". On utilise donc cette route, pas /patients/{id}.
     * Après mise à jour, on renvoie directement la liste.
     */
    @PostMapping("/patients/update")
    public String updatePatient(@ModelAttribute("patient") Patient payload,
                                HttpServletRequest request,
                                Model model) {

        if (payload.getId() != null) {
            patients.update(payload.getId(), payload, request);
        }

        List<Patient> all = patients.findAll(request);
        model.addAttribute("patients", all);
        return "patients";
    }

    /**
     * Suppression d'un patient.
     *
     * Le formulaire dans patients.html poste sur /patients/delete/{id}.
     * Après suppression, on renvoie directement la liste.
     */
    @PostMapping("/patients/delete/{id}")
    public String deletePatient(@PathVariable Long id,
                                HttpServletRequest request,
                                Model model) {

        patients.delete(id, request);

        List<Patient> all = patients.findAll(request);
        model.addAttribute("patients", all);
        return "patients";
    }

    /**
     * Historique des notes d'un patient.
     *
     * Appelé depuis patients.html :
     *   <form th:action="@{'/ui/patients/' + ${p.id} + '/notes'}" method="get">
     * Le Gateway enlève /ui → ici on reçoit /patients/{id}/notes.
     */
    @GetMapping("/patients/{id}/notes")
    public String showPatientNotes(@PathVariable Long id,
                                   Model model,
                                   HttpServletRequest request) {
        Patient patient = patients.getOne(id, request);
        List<Note> patientNotes = notes.findByPatient(id, request);

        model.addAttribute("patient", patient);
        model.addAttribute("notes", patientNotes);
        return "patient-notes";
    }

    /**
     * Rapport de risque de diabète pour un patient.
     *
     * Appelé depuis patients.html :
     *   <form th:action="@{'/ui/patients/' + ${p.id} + '/risk'}" method="get">
     * Le Gateway enlève /ui → ici on reçoit /patients/{id}/risk.
     */
    @GetMapping("/patients/{id}/risk")
    public String showRiskReport(@PathVariable Long id,
                                 Model model,
                                 HttpServletRequest request) {
        Patient patient = patients.getOne(id, request);
        RiskAssessmentResponse riskResponse = risk.getRisk(id, request);

        model.addAttribute("patient", patient);
        model.addAttribute("risk", riskResponse); // doit s'appeler "risk" pour risk-report.html
        return "risk-report";
    }
}
