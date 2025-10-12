package com.medilabo.patientui.model;

public class RiskAssessmentResponse {
    private Integer patientId;
    private String riskLevel; // e.g., NONE, BORDERLINE, IN_DANGER, EARLY_ONSET
    private int triggerCount;

    public Integer getPatientId() { return patientId; }
    public void setPatientId(Integer patientId) { this.patientId = patientId; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }

    public int getTriggerCount() { return triggerCount; }
    public void setTriggerCount(int triggerCount) { this.triggerCount = triggerCount; }
}
