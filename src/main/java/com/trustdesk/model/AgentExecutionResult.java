package com.trustdesk.model;

public class AgentExecutionResult {

    private String ticketId;
    private String action;
    private String status;
    private String message;
    private String resourceId;
    private String agentResponse;

    public AgentExecutionResult() {
    }

    public AgentExecutionResult(
            String ticketId,
            String action,
            String status,
            String message,
            String resourceId,
            String agentResponse) {

        this.ticketId = ticketId;
        this.action = action;
        this.status = status;
        this.message = message;
        this.resourceId = resourceId;
        this.agentResponse = agentResponse;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
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

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getAgentResponse() {
        return agentResponse;
    }

    public void setAgentResponse(String agentResponse) {
        this.agentResponse = agentResponse;
    }
}