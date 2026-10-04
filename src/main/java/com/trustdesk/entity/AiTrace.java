package com.trustdesk.entity;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "ai_traces")
public class AiTrace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ticket_id", nullable = false)
    private String ticketId;

    @Column(name = "run_type", nullable = false)
    private String runType;

    @Column(name = "retrieved_document_ids", columnDefinition = "TEXT")
    private String retrievedDocumentIds;

    @Column(name = "tool_actions", columnDefinition = "TEXT")
    private String toolActions;

    @Column(name = "guardrail_result", columnDefinition = "TEXT")
    private String guardrailResult;

    @Column(name = "final_status")
    private String finalStatus;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    public AiTrace() {
    }

    public Long getId() {
        return id;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getRunType() {
        return runType;
    }

    public void setRunType(String runType) {
        this.runType = runType;
    }

    public String getRetrievedDocumentIds() {
        return retrievedDocumentIds;
    }

    public void setRetrievedDocumentIds(String retrievedDocumentIds) {
        this.retrievedDocumentIds = retrievedDocumentIds;
    }

    public String getToolActions() {
        return toolActions;
    }

    public void setToolActions(String toolActions) {
        this.toolActions = toolActions;
    }

    public String getGuardrailResult() {
        return guardrailResult;
    }

    public void setGuardrailResult(String guardrailResult) {
        this.guardrailResult = guardrailResult;
    }

    public String getFinalStatus() {
        return finalStatus;
    }

    public void setFinalStatus(String finalStatus) {
        this.finalStatus = finalStatus;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}