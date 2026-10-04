package com.trustdesk.controller;

import com.trustdesk.entity.Ticket;
import com.trustdesk.model.TriageResult;
import com.trustdesk.service.TicketService;
import com.trustdesk.service.TriageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets")
public class TriageController {

    private final TicketService ticketService;
    private final TriageService triageService;

    public TriageController(
            TicketService ticketService,
            TriageService triageService) {
        this.ticketService = ticketService;
        this.triageService = triageService;
    }

    @PostMapping("/{ticketId}/triage")
    public ResponseEntity<?> triageTicket(
            @PathVariable String ticketId) {

        return ticketService.getTicketById(ticketId)
                .map(ticket -> {
                    TriageResult result = triageService.triage(ticket);
                    return ResponseEntity.ok(result);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}