package com.trustdesk.model;

import com.trustdesk.entity.Customer;
import com.trustdesk.entity.Order;
import com.trustdesk.entity.Ticket;

import java.util.List;

public class AgentContext {

    private Ticket ticket;
    private Customer customer;
    private Order order;
    private List<String> knowledgeDocumentIds;

    public AgentContext() {
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public List<String> getKnowledgeDocumentIds() {
        return knowledgeDocumentIds;
    }

    public void setKnowledgeDocumentIds(List<String> knowledgeDocumentIds) {
        this.knowledgeDocumentIds = knowledgeDocumentIds;
    }
}