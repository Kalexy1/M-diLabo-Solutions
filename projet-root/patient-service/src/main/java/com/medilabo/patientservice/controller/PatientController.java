package com.medilabo.patientservice.controller;

import com.medilabo.patientservice.model.Patient;
import com.medilabo.patientservice.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des patients.
 * <p>
 * Fournit les opérations CRUD permettant de créer, lire, mettre à jour
 * et supprimer les patients. L’accès aux endpoints est restreint aux
 * utilisateurs disposant des rôles {@code ORGANISATEUR} ou {@code PRATICIEN}.
 * </p>
 */
@RestController
@RequestMapping("/api/patients")
public class PatientController {

    /**
     * Service gérant la logique métier liée aux patients.
     */
    private final PatientService service;

    /**
     * Constructeur injectant le service de gestion des patients.
     *
     * @param service instance du {@link PatientService}
     */
    public PatientController(PatientService service) {
        this.service = service;
    }

    /**
     * Récupère la liste de tous les patients ou effectue une recherche
     * par nom de famille si le paramètre {@code q} est fourni.
     *
     * @param q nom ou fragment de nom à rechercher (optionnel)
     * @return la liste des patients correspondants
     */
    @GetMapping
    public List<Patient> findAll(@RequestParam(value = "q", required = false) String q) {
        if (q != null && !q.isBlank()) {
            return service.searchByLastName(q);
        }
        return service.findAll();
    }

    /**
     * Récupère un patient à partir de son identifiant unique.
     *
     * @param id identifiant du patient
     * @return le patient correspondant
     */
    @GetMapping("/{id}")
    public Patient getOne(@PathVariable Long id) {
        return service.getById(id);
    }

    /**
     * Crée un nouveau patient dans la base de données.
     *
     * @param payload objet {@link Patient} à créer
     * @return le patient créé
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Patient create(@Valid @RequestBody Patient payload) {
        return service.create(payload);
    }

    /**
     * Met à jour les informations d’un patient existant.
     *
     * @param id      identifiant du patient à modifier
     * @param payload objet {@link Patient} contenant les nouvelles données
     * @return le patient mis à jour
     */
    @PutMapping("/{id}")
    public Patient update(@PathVariable Long id, @Valid @RequestBody Patient payload) {
        return service.update(id, payload);
    }

    /**
     * Supprime un patient à partir de son identifiant unique.
     *
     * @param id identifiant du patient à supprimer
     */
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
