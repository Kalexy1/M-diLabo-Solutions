package com.medilabo.patientservice.controller;

import com.medilabo.patientservice.model.Patient;
import com.medilabo.patientservice.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des patients.
 * <p>
 * Fournit des endpoints pour les opérations CRUD sur les entités {@link Patient}.
 * </p>
 */
@RestController
@RequestMapping("/patients")
public class PatientController {

    @Autowired
    private PatientRepository repository;

    /**
     * Récupère la liste de tous les patients.
     *
     * @return une liste de tous les {@link Patient}
     */
    @GetMapping
    public List<Patient> getAll() {
        return repository.findAll();
    }

    /**
     * Récupère un patient par son identifiant.
     *
     * @param id l'identifiant du patient à rechercher
     * @return une réponse contenant le {@link Patient} trouvé ou un code 404 si non trouvé
     */
    @GetMapping("/{id}")
    public ResponseEntity<Patient> getOne(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Crée un nouveau patient dans la base de données.
     *
     * @param patient l'objet {@link Patient} à créer
     * @return le {@link Patient} sauvegardé
     */
    @PostMapping
    public Patient create(@RequestBody Patient patient) {
        return repository.save(patient);
    }

    /**
     * Met à jour un patient existant.
     *
     * @param id l'identifiant du patient à mettre à jour
     * @param updatedPatient les nouvelles informations du patient
     * @return une réponse contenant le {@link Patient} mis à jour ou un code 404 si non trouvé
     */
    @PutMapping("/{id}")
    public ResponseEntity<Patient> update(@PathVariable Long id, @RequestBody Patient updatedPatient) {
        return repository.findById(id).map(patient -> {
            patient.setPrenom(updatedPatient.getPrenom());
            patient.setNom(updatedPatient.getNom());
            patient.setDateNaissance(updatedPatient.getDateNaissance());
            patient.setGenre(updatedPatient.getGenre());
            patient.setAdresse(updatedPatient.getAdresse());
            patient.setTelephone(updatedPatient.getTelephone());
            return ResponseEntity.ok(repository.save(patient));
        }).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Supprime un patient par son identifiant.
     *
     * @param id l'identifiant du patient à supprimer
     * @return une réponse 200 OK si le patient a été supprimé, ou 404 Not Found s'il n'existe pas
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
