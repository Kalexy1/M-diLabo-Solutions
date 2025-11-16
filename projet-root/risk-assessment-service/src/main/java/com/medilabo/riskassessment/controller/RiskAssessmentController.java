package com.medilabo.riskassessment.controller;

import com.medilabo.riskassessment.dto.RiskAssessmentResponse;
import com.medilabo.riskassessment.service.RiskAssessmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur REST du microservice <strong>risk-assessment-service</strong>.
 * <p>
 * Ce contrôleur expose l’API {@code /api/risk/{patientId}} permettant de calculer
 * le niveau de risque de diabète d’un patient à partir de ses informations médicales
 * et de son historique de notes.
 * </p>
 * <p>
 * L’accès à cet endpoint est restreint aux utilisateurs ayant le rôle {@code PRATICIEN}.
 * L’authentification est gérée par un serveur de ressources JWT configuré dans la
 * couche de sécurité.
 * </p>
 */
@RestController
@RequestMapping(path = "/api/risk")
public class RiskAssessmentController {

    /**
     * Service applicatif chargé du calcul du niveau de risque.
     */
    private final RiskAssessmentService riskService;

    /**
     * Constructeur du contrôleur.
     *
     * @param riskService le service responsable du calcul du risque
     */
    public RiskAssessmentController(RiskAssessmentService riskService) {
        this.riskService = riskService;
    }

    /**
     * Évalue le niveau de risque de diabète pour un patient donné.
     *
     * @param patientId l’identifiant du patient
     * @return une {@link ResponseEntity} contenant le résultat du calcul du risque
     */
    @GetMapping(path = "/{patientId}")
    public ResponseEntity<RiskAssessmentResponse> getRisk(@PathVariable Long patientId) {
        return ResponseEntity.ok(riskService.assessRiskDetailed(patientId));
    }
}
