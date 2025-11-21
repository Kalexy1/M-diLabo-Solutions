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
 * Contrôleur UI du microservice patient-ui-service.
 *
 * <p>
 * Les routes exposées par ce contrôleur sont appelées via le Gateway
 * sous le préfixe {@code /ui/**}. Le Gateway retire ce préfixe avant
 * de proxyfier vers patient-ui, de sorte qu'ici les mappings ne
 * commencent pas par {@code /ui}.
 * </p>
 */
@Controller
public class PatientController {

    private final PatientService patients;
    private final NoteService notes;
    private final RiskService risk;

    /**
     * Construit le contrôleur UI en injectant les services nécessaires.
     *
     * @param patients service de gestion des patients
     * @param notes    service de gestion des notes
     * @param risk     service d'évaluation du risque de diabète
     */
    public PatientController(PatientService patients, NoteService notes, RiskService risk) {
        this.patients = patients;
        this.notes = notes;
        this.risk = risk;
    }

    /**
     * Page d'accueil interne de l'UI.
     *
     * <p>
     * Côté navigateur, la page est appelée via {@code /ui} ou {@code /ui/}.
     * Le Gateway retire le préfixe {@code /ui} et route la requête vers
     * ce mapping ({@code ""} ou {@code "/"}), qui renvoie directement
     * la liste des patients.
     * </p>
     *
     * @param model   modèle Thymeleaf
     * @param request requête HTTP courante
     * @return le nom de la vue affichant la liste des patients
     */
    @GetMapping({ "", "/" })
    public String home(Model model, HttpServletRequest request) {
        return listPatients(model, request);
    }

    /**
     * Affiche la liste des patients.
     *
     * @param model   modèle Thymeleaf
     * @param request requête HTTP courante
     * @return le nom de la vue listant les patients
     */
    @GetMapping("/patients")
    public String listPatients(Model model, HttpServletRequest request) {
        List<Patient> all = patients.findAll(request);
        model.addAttribute("patients", all);
        return "patients";
    }

    /**
     * Affiche le formulaire d'ajout d'un patient.
     *
     * @param model modèle Thymeleaf
     * @return le nom de la vue du formulaire d'ajout
     */
    @GetMapping("/patients/new")
    public String showAddForm(Model model) {
        model.addAttribute("patient", new Patient());
        return "add-patient";
    }

    /**
     * Crée un nouveau patient.
     *
     * <p>
     * Après création, la liste des patients est rechargée et renvoyée
     * directement sous forme de vue, sans redirection HTTP.
     * </p>
     *
     * @param payload données du patient à créer
     * @param request requête HTTP courante
     * @param model   modèle Thymeleaf
     * @return le nom de la vue listant les patients
     */
    @PostMapping("/patients")
    public String createPatient(@ModelAttribute("patient") Patient payload,
                                HttpServletRequest request,
                                Model model) {

        patients.create(payload, request);

        List<Patient> all = patients.findAll(request);
        model.addAttribute("patients", all);
        return "patients";
    }

    /**
     * Affiche le formulaire d'édition d'un patient existant.
     *
     * @param id      identifiant du patient à modifier
     * @param model   modèle Thymeleaf
     * @param request requête HTTP courante
     * @return le nom de la vue d'édition
     */
    @GetMapping("/patients/edit/{id}")
    public String showEditForm(@PathVariable Long id,
                               Model model,
                               HttpServletRequest request) {
        Patient patient = patients.getOne(id, request);
        model.addAttribute("patient", patient);
        return "edit-patient";
    }

    /**
     * Met à jour un patient existant.
     *
     * <p>
     * Le formulaire d'édition envoie les données vers {@code /patients/update}
     * avec un champ caché {@code id}. Après mise à jour, la liste des patients
     * est rechargée et renvoyée.
     * </p>
     *
     * @param payload données modifiées du patient
     * @param request requête HTTP courante
     * @param model   modèle Thymeleaf
     * @return le nom de la vue listant les patients
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
     * Supprime un patient.
     *
     * @param id      identifiant du patient à supprimer
     * @param request requête HTTP courante
     * @param model   modèle Thymeleaf
     * @return le nom de la vue listant les patients
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
     * Affiche l'historique des notes d'un patient.
     *
     * <p>
     * Côté navigateur, l'URL utilisée est de la forme
     * {@code /ui/patients/{id}/notes}. Le Gateway supprime {@code /ui}
     * et route ici sur {@code /patients/{id}/notes}.
     * </p>
     *
     * @param id      identifiant du patient
     * @param model   modèle Thymeleaf
     * @param request requête HTTP courante
     * @return le nom de la vue affichant les notes du patient
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
     * Affiche le rapport de risque de diabète pour un patient.
     *
     * <p>
     * L'URL appelée côté navigateur est {@code /ui/patients/{id}/risk}.
     * Après suppression du préfixe {@code /ui} par le Gateway, la requête
     * atteint ce mapping {@code /patients/{id}/risk}.
     * </p>
     *
     * @param id      identifiant du patient
     * @param model   modèle Thymeleaf
     * @param request requête HTTP courante
     * @return le nom de la vue présentant le rapport de risque
     */
    @GetMapping("/patients/{id}/risk")
    public String showRiskReport(@PathVariable Long id,
                                 Model model,
                                 HttpServletRequest request) {
        Patient patient = patients.getOne(id, request);
        RiskAssessmentResponse riskResponse = risk.getRisk(id, request);

        model.addAttribute("patient", patient);
        model.addAttribute("risk", riskResponse);
        return "risk-report";
    }

    /**
     * Ajoute une nouvelle note pour un patient.
     *
     * <p>
     * Côté navigateur, le formulaire poste sur
     * {@code /ui/patients/{patientId}/notes}. Le Gateway enlève
     * {@code /ui} et la requête arrive ici sur
     * {@code POST /patients/{patientId}/notes}.
     * </p>
     *
     * @param patientId identifiant du patient concerné
     * @param content   contenu de la note à ajouter
     * @param request   requête HTTP courante
     * @param model     modèle Thymeleaf
     * @return le nom de la vue affichant les notes du patient
     */
    @PostMapping("/patients/{patientId}/notes")
    public String addNote(@PathVariable Long patientId,
                          @RequestParam("content") String content,
                          HttpServletRequest request,
                          Model model) {

        Note note = new Note();
        note.setContent(content);

        notes.createForPatient(patientId, note, request);

        Patient patient = patients.getOne(patientId, request);
        List<Note> patientNotes = notes.findByPatient(patientId, request);

        model.addAttribute("patient", patient);
        model.addAttribute("notes", patientNotes);

        return "patient-notes";
    }
}
