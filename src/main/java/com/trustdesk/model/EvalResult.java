package com.trustdesk.model;

import java.util.List;

public class EvalResult {

    private String caseId;
    private String ticketId;

    private String actualCategory;
    private String actualPriority;
    private boolean actualEscalation;

    private boolean categoryCorrect;
    private boolean priorityCorrect;
    private boolean escalationCorrect;

    private List<String> expectedCitations;
    private List<String> actualCitations;

    private double citationCoverage;

    private boolean unsafeActionExpected;
    private boolean unsafeActionBlocked;

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

    public String getActualCategory() {
        return actualCategory;
    }

    public void setActualCategory(String actualCategory) {
        this.actualCategory = actualCategory;
    }

    public String getActualPriority() {
        return actualPriority;
    }

    public void setActualPriority(String actualPriority) {
        this.actualPriority = actualPriority;
    }

    public boolean isActualEscalation() {
        return actualEscalation;
    }

    public void setActualEscalation(boolean actualEscalation) {
        this.actualEscalation = actualEscalation;
    }

    public boolean isCategoryCorrect() {
        return categoryCorrect;
    }

    public void setCategoryCorrect(boolean categoryCorrect) {
        this.categoryCorrect = categoryCorrect;
    }

    public boolean isPriorityCorrect() {
        return priorityCorrect;
    }

    public void setPriorityCorrect(boolean priorityCorrect) {
        this.priorityCorrect = priorityCorrect;
    }

    public boolean isEscalationCorrect() {
        return escalationCorrect;
    }

    public void setEscalationCorrect(boolean escalationCorrect) {
        this.escalationCorrect = escalationCorrect;
    }

    public List<String> getExpectedCitations() {
        return expectedCitations;
    }

    public void setExpectedCitations(List<String> expectedCitations) {
        this.expectedCitations = expectedCitations;
    }

    public List<String> getActualCitations() {
        return actualCitations;
    }

    public void setActualCitations(List<String> actualCitations) {
        this.actualCitations = actualCitations;
    }

    public double getCitationCoverage() {
        return citationCoverage;
    }

    public void setCitationCoverage(double citationCoverage) {
        this.citationCoverage = citationCoverage;
    }

    public boolean isUnsafeActionExpected() {
        return unsafeActionExpected;
    }

    public void setUnsafeActionExpected(boolean unsafeActionExpected) {
        this.unsafeActionExpected = unsafeActionExpected;
    }

    public boolean isUnsafeActionBlocked() {
        return unsafeActionBlocked;
    }

    public void setUnsafeActionBlocked(boolean unsafeActionBlocked) {
        this.unsafeActionBlocked = unsafeActionBlocked;
    }
}