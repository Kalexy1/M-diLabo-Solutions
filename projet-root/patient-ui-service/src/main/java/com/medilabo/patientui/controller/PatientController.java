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

@Controller
@RequestMapping("/ui/patients")
public class PatientController {

    private final PatientService patients;
    private final NoteService notes;
    private final RiskService risk;

    public PatientController(PatientService patients, NoteService notes, RiskService risk) {
        this.patients = patients;
        this.notes = notes;
        this.risk = risk;
    }

    private String jwtOrNull(HttpServletRequest req) {
        try {
            return extractJwt(req, "JWT_TOKEN");
        } catch (Exception ignored) {
            return null;
        }
    }

    // ---- Liste des patients ----
    @GetMapping
    @PreAuthorize("hasAnyRole('ORGANISATEUR','PRATICIEN')")
    public String list(Model model, HttpServletRequest req) {
        String jwt = jwtOrNull(req);
        model.addAttribute("patients", patients.findAll(jwt));
        return "patients"; // templates/patients.html
    }

    // ---- Détails d’un patient + notes + risque ----
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
        return "patient-notes"; // templates/patient-notes.html
    }

    // ---- Formulaire d’ajout ----
    @GetMapping("/add")
    @PreAuthorize("hasRole('ORGANISATEUR')")
    public String showAddForm(Model model) {
        model.addAttribute("patient", new Patient());
        return "add-patient"; // templates/add-patient.html
    }

    // ---- Création du patient ----
    @PostMapping
    @PreAuthorize("hasRole('ORGANISATEUR')")
    public String create(@ModelAttribute Patient payload, HttpServletRequest req) {
        String jwt = jwtOrNull(req);
        var saved = patients.create(payload, jwt);
        Long id = (saved != null) ? saved.getId() : null;
        return (id != null)
                ? "redirect:/ui/patients/" + id
                : "redirect:/ui/patients";
    }

    // ---- Formulaire de modification ----
    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('ORGANISATEUR')")
    public String showEditForm(@PathVariable Long id, Model model, HttpServletRequest req) {
        String jwt = jwtOrNull(req);
        var patient = patients.getOne(id, jwt);
        model.addAttribute("patient", patient);
        return "edit-patient"; // templates/edit-patient.html
    }

    // ---- Mise à jour du patient ----
    @PostMapping("/{id}")
    @PreAuthorize("hasRole('ORGANISATEUR')")
    public String update(@PathVariable Long id, @ModelAttribute Patient payload, HttpServletRequest req) {
        String jwt = jwtOrNull(req);
        patients.update(id, payload, jwt);
        return "redirect:/ui/patients/" + id;
    }

    // ---- Suppression du patient ----
    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ORGANISATEUR')")
    public String delete(@PathVariable Long id, HttpServletRequest req) {
        String jwt = jwtOrNull(req);
        patients.delete(id, jwt);
        return "redirect:/ui/patients";
    }

    // ---- Page d’évaluation du risque ----
    @GetMapping("/{id}/risk")
    @PreAuthorize("hasAnyRole('ORGANISATEUR','PRATICIEN')")
    public String riskReport(@PathVariable Long id, Model model, HttpServletRequest req) {
        String jwt = jwtOrNull(req);
        var patient = patients.getOne(id, jwt);
        var riskLevel = risk.getRisk(id, jwt);

        model.addAttribute("patient", patient);
        model.addAttribute("riskLevel", riskLevel);
        return "risk-report"; // templates/risk-report.html
    }
}
