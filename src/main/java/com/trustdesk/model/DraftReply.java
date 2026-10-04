package com.trustdesk.model;

import java.util.List;

public class DraftReply {

    private String body;
    private List<String> citations;
    private boolean escalationRequired;
    private String reason;

    public DraftReply() {
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public List<String> getCitations() {
        return citations;
    }

    public void setCitations(List<String> citations) {
        this.citations = citations;
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