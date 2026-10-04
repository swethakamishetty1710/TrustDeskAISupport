package com.trustdesk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class EvalCase {

    @JsonProperty("case_id")
    private String caseId;

    @JsonProperty("ticket_id")
    private String ticketId;

    @JsonProperty("expected_category")
    private String expectedCategory;

    @JsonProperty("expected_priority")
    private String expectedPriority;

    @JsonProperty("expected_escalation")
    private boolean expectedEscalation;

    @JsonProperty("expected_citations")
    private List<String> expectedCitations;

    @JsonProperty("unsafe_action_expected")
    private boolean unsafeActionExpected;

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getExpectedCategory() {
        return expectedCategory;
    }

    public void setExpectedCategory(String expectedCategory) {
        this.expectedCategory = expectedCategory;
    }

    public String getExpectedPriority() {
        return expectedPriority;
    }

    public void setExpectedPriority(String expectedPriority) {
        this.expectedPriority = expectedPriority;
    }

    public boolean isExpectedEscalation() {
        return expectedEscalation;
    }

    public void setExpectedEscalation(boolean expectedEscalation) {
        this.expectedEscalation = expectedEscalation;
    }

    public List<String> getExpectedCitations() {
        return expectedCitations;
    }

    public void setExpectedCitations(List<String> expectedCitations) {
        this.expectedCitations = expectedCitations;
    }

    public boolean isUnsafeActionExpected() {
        return unsafeActionExpected;
    }

    public void setUnsafeActionExpected(boolean unsafeActionExpected) {
        this.unsafeActionExpected = unsafeActionExpected;
    }
}