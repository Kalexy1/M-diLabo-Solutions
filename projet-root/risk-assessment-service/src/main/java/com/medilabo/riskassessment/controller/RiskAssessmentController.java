package com.medilabo.riskassessment.controller;

import com.medilabo.riskassessment.dto.RiskAssessmentResponse;
import com.medilabo.riskassessment.service.RiskAssessmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur REST du microservice Risk Assessment.
 * <p>
 * Fournit un endpoint pour évaluer le niveau de risque de diabète d’un patient
 * à partir de son identifiant.
 * </p>
 */
@RestController
@RequestMapping("/assess")
public class RiskAssessmentController {

    @Autowired
    private RiskAssessmentService riskService;

    /**
     * Évalue le risque de diabète d’un patient donné en appelant le service métier.
     *
     * @param patientId identifiant du patient à analyser
     * @return un objet {@link RiskAssessmentResponse} contenant le niveau de risque et les informations patient
     */
    @GetMapping("/{patientId}")
    public ResponseEntity<RiskAssessmentResponse> getRisk(@PathVariable Long patientId) {
        RiskAssessmentResponse response = riskService.assessRiskDetailed(patientId);
        return ResponseEntity.ok(response);
    }
}
