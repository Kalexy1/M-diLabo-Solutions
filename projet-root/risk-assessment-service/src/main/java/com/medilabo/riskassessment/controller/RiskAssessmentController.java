package com.medilabo.riskassessment.controller;

import com.medilabo.riskassessment.dto.RiskAssessmentResponse;
import com.medilabo.riskassessment.service.RiskAssessmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur REST du microservice <strong>risk-assessment-service</strong>.
 *
 * <p>
 * Expose l’endpoint {@code GET /api/risk/{patientId}} permettant d'évaluer
 * le niveau de risque de diabète d’un patient en interrogeant :
 * </p>
 * <ul>
 *     <li>le microservice <strong>patient-service</strong> pour les données du patient,</li>
 *     <li>le microservice <strong>note-service</strong> pour l’historique médical.</li>
 * </ul>
 *
 * <p>
 * L’accès à cet endpoint est contrôlé par la configuration de sécurité :
 * seuls les utilisateurs authentifiés (via JWT) peuvent y accéder.
 * </p>
 */
@RestController
@RequestMapping(path = "/api/risk")
public class RiskAssessmentController {

    /**
     * Service chargé du calcul du risque de diabète.
     */
    private final RiskAssessmentService riskService;

    /**
     * Constructeur injectant le service de calcul du risque.
     *
     * @param riskService service applicatif responsable de l’évaluation du risque
     */
    public RiskAssessmentController(RiskAssessmentService riskService) {
        this.riskService = riskService;
    }

    /**
     * Évalue et retourne le niveau de risque pour un patient donné.
     *
     * @param patientId identifiant du patient
     * @return une réponse contenant les informations du patient et son niveau de risque
     */
    @GetMapping("/{patientId}")
    public ResponseEntity<RiskAssessmentResponse> getRisk(@PathVariable Long patientId) {
        return ResponseEntity.ok(riskService.assessRiskDetailed(patientId));
    }
}
