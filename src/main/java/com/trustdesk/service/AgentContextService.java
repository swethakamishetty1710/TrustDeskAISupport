package com.trustdesk.service;

import com.trustdesk.entity.Customer;
import com.trustdesk.entity.KnowledgeDocument;
import com.trustdesk.entity.Order;
import com.trustdesk.entity.Ticket;
import com.trustdesk.model.AgentContext;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgentContextService {

    private final TicketService ticketService;
    private final KnowledgeSearchService knowledgeSearchService;

    public AgentContextService(
            TicketService ticketService,
            KnowledgeSearchService knowledgeSearchService) {

        this.ticketService = ticketService;
        this.knowledgeSearchService = knowledgeSearchService;
    }

    public AgentContext buildContext(String ticketId) {

        // 1. Get ticket
        Ticket ticket = ticketService.getTicketById(ticketId)
                .orElseThrow(() ->
                        new RuntimeException("Ticket not found: " + ticketId));

        // 2. Get customer
        Customer customer = ticketService
                .getCustomerForTicket(ticket)
                .orElse(null);

        // 3. Get order
        Order order = ticketService
                .getOrderForTicket(ticket)
                .orElse(null);

        // 4. Build search query from ticket
        String searchQuery =
                ticket.getSubject() + " " + ticket.getBody();

        // 5. Search Knowledge Base
        List<KnowledgeDocument> documents =
                knowledgeSearchService.search(searchQuery);

        // 6. Extract document IDs
        List<String> documentIds = documents.stream()
                .map(KnowledgeDocument::getDocId)
                .toList();

        // 7. Create AgentContext
        AgentContext context = new AgentContext();

        context.setTicket(ticket);
        context.setCustomer(customer);
        context.setOrder(order);
        context.setKnowledgeDocumentIds(documentIds);

        return context;
    }
}