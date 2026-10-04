package com.trustdesk.model;

import java.util.List;

public class EvalSummary {

    private int totalCases;

    private int categoryCorrect;
    private int priorityCorrect;
    private int escalationCorrect;

    private double triageAccuracy;
    private double escalationAccuracy;
    private double citationCoverage;
    private double unsafeActionBlockRate;

    private List<EvalResult> results;

    public int getTotalCases() {
        return totalCases;
    }

    public void setTotalCases(int totalCases) {
        this.totalCases = totalCases;
    }

    public int getCategoryCorrect() {
        return categoryCorrect;
    }

    public void setCategoryCorrect(int categoryCorrect) {
        this.categoryCorrect = categoryCorrect;
    }

    public int getPriorityCorrect() {
        return priorityCorrect;
    }

    public void setPriorityCorrect(int priorityCorrect) {
        this.priorityCorrect = priorityCorrect;
    }

    public int getEscalationCorrect() {
        return escalationCorrect;
    }

    public void setEscalationCorrect(int escalationCorrect) {
        this.escalationCorrect = escalationCorrect;
    }

    public double getTriageAccuracy() {
        return triageAccuracy;
    }

    public void setTriageAccuracy(double triageAccuracy) {
        this.triageAccuracy = triageAccuracy;
    }

    public double getEscalationAccuracy() {
        return escalationAccuracy;
    }

    public void setEscalationAccuracy(double escalationAccuracy) {
        this.escalationAccuracy = escalationAccuracy;
    }

    public double getCitationCoverage() {
        return citationCoverage;
    }

    public void setCitationCoverage(double citationCoverage) {
        this.citationCoverage = citationCoverage;
    }

    public double getUnsafeActionBlockRate() {
        return unsafeActionBlockRate;
    }

    public void setUnsafeActionBlockRate(double unsafeActionBlockRate) {
        this.unsafeActionBlockRate = unsafeActionBlockRate;
    }

    public List<EvalResult> getResults() {
        return results;
    }

    public void setResults(List<EvalResult> results) {
        this.results = results;
    }
}