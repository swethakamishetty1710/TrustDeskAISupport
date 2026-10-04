package com.trustdesk.service;

import com.trustdesk.entity.Customer;
import com.trustdesk.entity.Order;
import com.trustdesk.entity.Ticket;
import com.trustdesk.model.AgentActionDecision;
import com.trustdesk.model.ToolActionRequest;
import org.springframework.stereotype.Service;

@Service
public class AgentActionService {

    private final TicketService ticketService;
    private final ToolActionService toolActionService;
    private final AgentDecisionAiService agentDecisionAiService;

    public AgentActionService(
            TicketService ticketService,
            ToolActionService toolActionService,
            AgentDecisionAiService agentDecisionAiService) {

        this.ticketService = ticketService;
        this.toolActionService = toolActionService;
        this.agentDecisionAiService = agentDecisionAiService;
    }

    public ToolActionRequest createActionForTicket(
            String ticketId) {

        /*
         * 1. Load the ticket.
         */
        Ticket ticket = ticketService
                .getTicketById(ticketId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Ticket not found: " + ticketId
                        ));

        /*
         * 2. Load customer context.
         */
        Customer customer = ticketService
                .getCustomerForTicket(ticket)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Customer not found for ticket: "
                                        + ticketId
                        ));

        /*
         * 3. Load order context.
         */
        Order order = ticketService
                .getOrderForTicket(ticket)
                .orElse(null);

        /*
         * 4. Ask the AI Agent to decide whether
         *    an operational action is appropriate.
         *
         *    The AI Agent internally performs RAG
         *    against the knowledge base.
         */
        AgentActionDecision decision =
                agentDecisionAiService.decideAction(
                        ticket,
                        customer,
                        order
                );

        /*
         * 5. If the AI does not recommend an action,
         *    do not create a tool action.
         */
        if (!decision.isActionRecommended()
                || decision.getAction() == null
                || "none".equalsIgnoreCase(
                decision.getAction())) {

            return null;
        }

        /*
         * 6. Create a pending action.
         *
         *    IMPORTANT:
         *    This does NOT execute the action.
         *
         *    Human approval is still required for
         *    sensitive actions.
         */
        return toolActionService.createAction(
                decision.getAction(),
                ticket.getTicketId(),
                customer.getCustomerId(),
                order != null
                        ? order.getOrderId()
                        : null,
                decision.getReason(),
                decision.getRiskLevel(),
                decision.getEvidence()
        );
    }
}