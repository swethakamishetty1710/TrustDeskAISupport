package com.trustdesk.model;

public class TriageResult {

    private String category;
    private String priority;
    private String sentiment;
    private boolean escalationRequired;
    private String reason;

    public TriageResult() {
    }

    public TriageResult(
            String category,
            String priority,
            String sentiment,
            boolean escalationRequired,
            String reason) {
        this.category = category;
        this.priority = priority;
        this.sentiment = sentiment;
        this.escalationRequired = escalationRequired;
        this.reason = reason;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getSentiment() {
        return sentiment;
    }

    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
    }

    public boolean isEscalationRequired() {
        return escalationRequired;
    }

    public void setEscalationRequired(boolean escalationRequired) {
        this.escalationRequired = escalationRequired;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}