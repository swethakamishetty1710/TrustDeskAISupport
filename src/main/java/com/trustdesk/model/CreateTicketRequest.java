package com.trustdesk.model;

import jakarta.validation.constraints.NotBlank;

public class CreateTicketRequest {

    @NotBlank
    private String customerId;

    private String orderId;

    @NotBlank
    private String channel;

    @NotBlank
    private String subject;

    @NotBlank
    private String body;

    public CreateTicketRequest() {
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}