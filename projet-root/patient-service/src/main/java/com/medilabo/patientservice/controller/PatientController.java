package com.medilabo.patientservice.controller;

import com.medilabo.patientservice.model.Patient;
import com.medilabo.patientservice.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des patients.
 *
 * <p>Expose les opérations CRUD permettant de créer, lire, mettre à jour
 * et supprimer des patients. Toutes les routes se trouvent sous
 * {@code /api/patients}.</p>
 *
 * <p>L'accès aux endpoints est généralement sécurisé par Spring Security
 * et réservé aux utilisateurs possédant les rôles adéquats
 * (tels que {@code ORGANISATEUR} ou {@code PRATICIEN}).</p>
 */
@RestController
@RequestMapping("/api/patients")
public class PatientController {

    /**
     * Service métier gérant les opérations liées aux patients.
     */
    private final PatientService service;

    /**
     * Construit un contrôleur de gestion des patients.
     *
     * @param service service métier manipulant les entités {@link Patient}
     */
    public PatientController(PatientService service) {
        this.service = service;
    }

    /**
     * Récupère tous les patients ou, si un paramètre de recherche est fourni,
     * effectue une recherche par nom de famille.
     *
     * @param q fragment de nom à rechercher (optionnel)
     * @return liste des patients correspondants
     */
    @GetMapping
    public List<Patient> findAll(@RequestParam(value = "q", required = false) String q) {
        if (q != null && !q.isBlank()) {
            return service.searchByLastName(q);
        }
        return service.findAll();
    }

    /**
     * Récupère un patient via son identifiant unique.
     *
     * @param id identifiant du patient
     * @return patient correspondant
     */
    @GetMapping("/{id}")
    public Patient getOne(@PathVariable Long id) {
        return service.getById(id);
    }

    /**
     * Crée un nouveau patient dans la base de données.
     *
     * @param payload données du patient à créer
     * @return le patient nouvellement créé
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Patient create(@Valid @RequestBody Patient payload) {
        return service.create(payload);
    }

    /**
     * Met à jour les informations d’un patient existant.
     *
     * @param id identifiant du patient à modifier
     * @param payload données modifiées du patient
     * @return le patient mis à jour
     */
    @PutMapping("/{id}")
    public Patient update(@PathVariable Long id, @Valid @RequestBody Patient payload) {
        return service.update(id, payload);
    }

    /**
     * Supprime un patient à partir de son identifiant.
     *
     * @param id identifiant du patient à supprimer
     */
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
