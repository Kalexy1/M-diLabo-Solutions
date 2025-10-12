package com.medilabo.patientui.controller;

import com.medilabo.patientui.model.Patient;
import com.medilabo.patientui.service.NoteService;
import com.medilabo.patientui.service.PatientService;
import com.medilabo.patientui.service.RiskService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import static com.medilabo.patientui.web.JwtCookieUtil.extractJwt;

/**
 * Contrôleur UI pour la gestion des patients côté interface utilisateur.
 * <p>
 * Cette couche appelle les microservices via la Gateway en transmettant le JWT
 * extrait du cookie. Les vues Thymeleaf affichent la liste, le détail, les notes
 * et le niveau de risque des patients. Les règles d'accès sont appliquées par rôle :
 * <ul>
 *     <li>{@code ORGANISATEUR} : création, modification, suppression.</li>
 *     <li>{@code ORGANISATEUR} ou {@code PRATICIEN} : consultation et rapports.</li>
 * </ul>
 * </p>
 */
@Controller
@RequestMapping("/ui/patients")
public class PatientController {

    /**
     * Service de consultation et de gestion des patients (via Gateway).
     */
    private final PatientService patients;

    /**
     * Service d'accès aux notes médicales (via Gateway).
     */
    private final NoteService notes;

    /**
     * Service d’évaluation du risque de diabète (via Gateway).
     */
    private final RiskService risk;

    /**
     * Crée un contrôleur UI des patients.
     *
     * @param patients service d'accès aux patients.
     * @param notes    service d'accès aux notes.
     * @param risk     service d'évaluation du risque.
     */
    public PatientController(PatientService patients, NoteService notes, RiskService risk) {
        this.patients = patients;
        this.notes = notes;
        this.risk = risk;
    }

    /**
     * Extrait le JWT du cookie de la requête.
     *
     * @param req requête HTTP.
     * @return le jeton JWT ou {@code null} si absent/illisible.
     */
    private String jwtOrNull(HttpServletRequest req) {
        try {
            return extractJwt(req, "JWT_TOKEN");
        } catch (Exception ignored) {
            return null;
        }
    }

    /**
     * Affiche la liste des patients.
     *
     * @param model modèle de vue.
     * @param req   requête HTTP.
     * @return le nom de la vue de liste.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ORGANISATEUR','PRATICIEN')")
    public String list(Model model, HttpServletRequest req) {
        String jwt = jwtOrNull(req);
        model.addAttribute("patients", patients.findAll(jwt));
        return "patients";
    }

    /**
     * Affiche le détail d'un patient, ses notes et son niveau de risque.
     *
     * @param id    identifiant du patient.
     * @param model modèle de vue.
     * @param req   requête HTTP.
     * @return le nom de la vue de détail.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ORGANISATEUR','PRATICIEN')")
    public String details(@PathVariable Long id, Model model, HttpServletRequest req) {
        String jwt = jwtOrNull(req);
        var p = patients.getOne(id, jwt);
        var n = notes.findByPatient(id, jwt);
        var r = risk.getRisk(id, jwt);

        model.addAttribute("patient", p);
        model.addAttribute("notes", n);
        model.addAttribute("risk", r);
        return "patient-notes";
    }

    /**
     * Affiche le formulaire de création d'un patient.
     *
     * @param model modèle de vue.
     * @return le nom de la vue d'ajout.
     */
    @GetMapping("/add")
    @PreAuthorize("hasRole('ORGANISATEUR')")
    public String showAddForm(Model model) {
        model.addAttribute("patient", new Patient());
        return "add-patient";
    }

    /**
     * Crée un nouveau patient.
     *
     * @param payload données du patient à créer.
     * @param req     requête HTTP.
     * @return redirection vers la page de détail ou vers la liste.
     */
    @PostMapping
    @PreAuthorize("hasRole('ORGANISATEUR')")
    public String create(@ModelAttribute Patient payload, HttpServletRequest req) {
        String jwt = jwtOrNull(req);
        var saved = patients.create(payload, jwt);
        Long id = (saved != null) ? saved.getId() : null;
        return (id != null) ? "redirect:/ui/patients/" + id : "redirect:/ui/patients";
    }

    /**
     * Affiche le formulaire d'édition d'un patient.
     *
     * @param id    identifiant du patient.
     * @param model modèle de vue.
     * @param req   requête HTTP.
     * @return le nom de la vue d'édition.
     */
    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('ORGANISATEUR')")
    public String showEditForm(@PathVariable Long id, Model model, HttpServletRequest req) {
        String jwt = jwtOrNull(req);
        var patient = patients.getOne(id, jwt);
        model.addAttribute("patient", patient);
        return "edit-patient";
    }

    /**
     * Met à jour un patient existant.
     *
     * @param id      identifiant du patient.
     * @param payload données mises à jour.
     * @param req     requête HTTP.
     * @return redirection vers la page de détail.
     */
    @PostMapping("/{id}")
    @PreAuthorize("hasRole('ORGANISATEUR')")
    public String update(@PathVariable Long id, @ModelAttribute Patient payload, HttpServletRequest req) {
        String jwt = jwtOrNull(req);
        patients.update(id, payload, jwt);
        return "redirect:/ui/patients/" + id;
    }

    /**
     * Supprime un patient.
     *
     * @param id  identifiant du patient.
     * @param req requête HTTP.
     * @return redirection vers la liste des patients.
     */
    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ORGANISATEUR')")
    public String delete(@PathVariable Long id, HttpServletRequest req) {
        String jwt = jwtOrNull(req);
        patients.delete(id, jwt);
        return "redirect:/ui/patients";
    }

    /**
     * Affiche le rapport d'évaluation du risque pour un patient.
     *
     * @param id    identifiant du patient.
     * @param model modèle de vue.
     * @param req   requête HTTP.
     * @return le nom de la vue du rapport de risque.
     */
    @GetMapping("/{id}/risk")
    @PreAuthorize("hasAnyRole('ORGANISATEUR','PRATICIEN')")
    public String riskReport(@PathVariable Long id, Model model, HttpServletRequest req) {
        String jwt = jwtOrNull(req);
        var patient = patients.getOne(id, jwt);
        var riskLevel = risk.getRisk(id, jwt);

        model.addAttribute("patient", patient);
        model.addAttribute("riskLevel", riskLevel);
        return "risk-report";
    }
}
