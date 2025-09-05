package com.medilabo.riskassessment.controller;

import com.medilabo.riskassessment.dto.RiskAssessmentResponse;
import com.medilabo.riskassessment.service.RiskAssessmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Application: com.medilabo.riskassessment.controller
 * <p>
 * Classe <strong>RiskAssessmentController</strong>.
 * <br/>
 * Rôle : Expose les endpoints REST du microservice <em>risk-assessment-service</em>.
 * </p>
 * <p>
 * Ce contrôleur permet d’évaluer le risque de diabète d’un patient
 * en interrogeant le service métier {@link RiskAssessmentService}.
 * </p>
 */
@RestController
@RequestMapping("/assess")
public class RiskAssessmentController {

    @Autowired
    private RiskAssessmentService riskService;

    /**
     * Évalue le risque de diabète d’un patient en fonction :
     * <ul>
     *   <li>de ses données personnelles (âge, sexe) récupérées depuis <em>patient-service</em>,</li>
     *   <li>de son historique médical (notes) récupéré depuis <em>note-service</em>.</li>
     * </ul>
     * Accessible uniquement aux utilisateurs ayant le rôle <b>PRATICIEN</b>.
     *
     * @param patientId identifiant du patient à analyser
     * @return une réponse HTTP {@link ResponseEntity} contenant un objet
     *         {@link RiskAssessmentResponse} avec le niveau de risque évalué
     */
    @PreAuthorize("hasRole('PRATICIEN')")
    @GetMapping("/{patientId}")
    public ResponseEntity<RiskAssessmentResponse> getRisk(@PathVariable Long patientId) {
        RiskAssessmentResponse response = riskService.assessRiskDetailed(patientId);
        return ResponseEntity.ok(response);
    }
}
