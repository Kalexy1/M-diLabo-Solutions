package com.medilabo.riskassessment.controller;

import com.medilabo.riskassessment.dto.RiskAssessmentResponse;
import com.medilabo.riskassessment.service.RiskAssessmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Application: com.medilabo.riskassessment.controller
 *
 * Contrôleur REST du microservice risk-assessment-service.
 * Expose l’endpoint /risk/{patientId} qui calcule le niveau de risque
 * en s’appuyant sur RiskAssessmentService.
 *
 * Sécurité :
 *  - Accès restreint au rôle PRATICIEN via @PreAuthorize.
 *  - L’API est protégée par un Resource Server JWT (SecurityConfig).
 */
@RestController
@RequestMapping("/risk")
public class RiskAssessmentController {

    private final RiskAssessmentService riskService;

    public RiskAssessmentController(RiskAssessmentService riskService) {
        this.riskService = riskService;
    }

    /**
     * GET /risk/{patientId}
     * Retourne le niveau de risque pour le patient demandé.
     */
    @PreAuthorize("hasRole('PRATICIEN')")
    @GetMapping("/{patientId}")
    public ResponseEntity<RiskAssessmentResponse> getRisk(@PathVariable Long patientId) {
        return ResponseEntity.ok(riskService.assessRiskDetailed(patientId));
    }
}
