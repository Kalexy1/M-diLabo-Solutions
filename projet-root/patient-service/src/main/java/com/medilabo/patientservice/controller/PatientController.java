package com.medilabo.patientservice.controller;

import com.medilabo.patientservice.model.Patient;
import com.medilabo.patientservice.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Application: com.medilabo.patientservice.controller
 * <p>
 * Classe <strong>PatientController</strong>.
 * <br/>
 * Rôle : Contrôleur Spring MVC gérant les endpoints du microservice
 * <em>patient-service</em> et redirigeant vers le <em>patient-ui-service</em>
 * via la Gateway pour l'affichage Thymeleaf.
 * </p>
 * <p>
 * Droits d'accès :
 * <ul>
 *   <li><b>ORGANISATEUR</b> : ajout, modification, suppression et consultation.</li>
 *   <li><b>PRATICIEN</b> : consultation uniquement.</li>
 * </ul>
 * </p>
 */
@Controller
@RequestMapping("/patients")
public class PatientController {

    @Autowired
    private PatientService patientService;

    /**
     * Redirige vers la liste des patients du <em>patient-ui-service</em>.
     *
     * @return redirection vers {@code /ui/patients}.
     */
    @PreAuthorize("hasAnyRole('ORGANISATEUR', 'PRATICIEN')")
    @GetMapping
    public String redirectToUiList() {
        return "redirect:/ui/patients";
    }

    /**
     * Redirige vers le formulaire d’ajout d’un patient dans le <em>patient-ui-service</em>.
     *
     * @return redirection vers {@code /ui/patients/new}.
     */
    @PreAuthorize("hasRole('ORGANISATEUR')")
    @GetMapping("/new")
    public String redirectToUiAddForm() {
        return "redirect:/ui/patients/new";
    }

    /**
     * Sauvegarde un nouveau patient et redirige vers la liste des patients.
     *
     * @param patient patient à enregistrer.
     * @return redirection vers {@code /ui/patients}.
     */
    @PreAuthorize("hasRole('ORGANISATEUR')")
    @PostMapping
    public String savePatient(@ModelAttribute Patient patient) {
        patientService.savePatient(patient);
        return "redirect:/ui/patients";
    }

    /**
     * Redirige vers le formulaire d’édition d’un patient.
     * Si le patient n’existe pas, redirige vers la liste.
     *
     * @param id identifiant du patient.
     * @return redirection vers {@code /ui/patients/edit/{id}} ou {@code /ui/patients}.
     */
    @PreAuthorize("hasRole('ORGANISATEUR')")
    @GetMapping("/edit/{id}")
    public String redirectToUiEditForm(@PathVariable Long id) {
        Optional<Patient> p = patientService.getPatientById(id);
        return p.isPresent() ? "redirect:/ui/patients/edit/" + id : "redirect:/ui/patients";
    }

    /**
     * Met à jour les informations d’un patient existant puis redirige vers la liste.
     * Utilise {@link PatientService#updatePatient(Long, Patient)} afin d'appliquer
     * les modifications uniquement si le patient existe.
     *
     * @param patient patient contenant les nouvelles données (l'ID doit être renseigné).
     * @return redirection vers {@code /ui/patients}.
     */
    @PreAuthorize("hasRole('ORGANISATEUR')")
    @PostMapping("/update")
    public String updatePatient(@ModelAttribute Patient patient) {
        if (patient.getId() != null) {
            patientService.updatePatient(patient.getId(), patient);
        }
        return "redirect:/ui/patients";
    }

    /**
     * Supprime un patient (appel en POST recommandé pour supporter CSRF) puis redirige vers la liste.
     *
     * @param id identifiant du patient à supprimer.
     * @return redirection vers {@code /ui/patients}.
     */
    @PreAuthorize("hasRole('ORGANISATEUR')")
    @PostMapping("/delete/{id}")
    public String deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return "redirect:/ui/patients";
    }
}
