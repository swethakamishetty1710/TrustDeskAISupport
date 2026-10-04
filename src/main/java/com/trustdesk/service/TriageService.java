package com.trustdesk.service;

import com.trustdesk.entity.Ticket;
import com.trustdesk.model.TriageResult;
import org.springframework.stereotype.Service;

@Service
public class TriageService {

    public TriageResult triage(Ticket ticket) {

        String text = (
                ticket.getSubject() + " " + ticket.getBody()
        ).toLowerCase();

        String category;
        String priority;
        String sentiment;
        boolean escalationRequired;
        String reason;

        // Category
        if (text.contains("refund")
                || text.contains("refund me")
                || text.contains("return")) {
            category = "refund";
        } else if (text.contains("tracking")
                || text.contains("package")
                || text.contains("delivery")
                || text.contains("carrier")) {
            category = "shipping";
        } else if (text.contains("warranty")
                || text.contains("battery")
                || text.contains("swelling")
                || text.contains("defective")) {
            category = "warranty";
        } else if (text.contains("charge")
                || text.contains("payment")
                || text.contains("charged")) {
            category = "billing";
        } else if (text.contains("account")
                || text.contains("email")
                || text.contains("identity")
                || text.contains("system prompt")
                || text.contains("api key")) {
            category = "account_security";
        } else {
            category = "general";
        }

        // Priority
        if (text.contains("urgent")
                || text.contains("swelling")
                || text.contains("safety")) {
            priority = "urgent";
        } else if (text.contains("quickly")
                || text.contains("lost access")
                || text.contains("ignore identity")
                || text.contains("double charge")) {
            priority = "high";
        } else if (text.contains("damaged")
                || text.contains("cracked")) {
            priority = "medium";
        } else {
            priority = "low";
        }

        // Sentiment
        if (text.contains("frustrated")
                || text.contains("not moved")
                || text.contains("double charge")) {
            sentiment = "frustrated";
        } else if (text.contains("worried")
                || text.contains("swelling")
                || text.contains("failed")) {
            sentiment = "worried";
        } else {
            sentiment = "neutral";
        }

        // Escalation / guardrail conditions
        if (text.contains("ignore identity")
                || text.contains("system override")
                || text.contains("ignore all instructions")
                || text.contains("hidden system prompt")
                || text.contains("api key")
                || text.contains("internal notes")) {

            escalationRequired = true;
            reason = "Potential prompt injection or request for protected information.";

        } else if ("warranty".equals(category)
                && "urgent".equals(priority)) {

            escalationRequired = true;
            reason = "Potential product safety issue requires human review.";

        } else {
            escalationRequired = false;
            reason = "Ticket can proceed through standard support workflow.";
        }

        return new TriageResult(
                category,
                priority,
                sentiment,
                escalationRequired,
                reason
        );
    }
}