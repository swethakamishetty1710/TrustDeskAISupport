package com.trustdesk.model;

public class ToolExecutionResult {

    private String action;
    private String status;
    private String message;
    private String idempotencyKey;
    private String createdResourceId;

    public ToolExecutionResult() {
    }

    public ToolExecutionResult(
            String action,
            String status,
            String message,
            String idempotencyKey,
            String createdResourceId) {

        this.action = action;
        this.status = status;
        this.message = message;
        this.idempotencyKey = idempotencyKey;
        this.createdResourceId = createdResourceId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public String getCreatedResourceId() {
        return createdResourceId;
    }

    public void setCreatedResourceId(String createdResourceId) {
        this.createdResourceId = createdResourceId;
    }
}