package com.trustdesk.model;

import java.util.List;

public class AgentActionDecision {

    private String action;

    private boolean actionRecommended;

    private String reason;

    private String riskLevel;

    private List<String> evidence;

    private boolean humanApprovalRequired;

    public AgentActionDecision() {
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public boolean isActionRecommended() {
        return actionRecommended;
    }

    public void setActionRecommended(boolean actionRecommended) {
        this.actionRecommended = actionRecommended;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public List<String> getEvidence() {
        return evidence;
    }

    public void setEvidence(List<String> evidence) {
        this.evidence = evidence;
    }

    public boolean isHumanApprovalRequired() {
        return humanApprovalRequired;
    }

    public void setHumanApprovalRequired(boolean humanApprovalRequired) {
        this.humanApprovalRequired = humanApprovalRequired;
    }
}