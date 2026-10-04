package com.trustdesk.controller;

import com.trustdesk.entity.Customer;
import com.trustdesk.entity.Order;
import com.trustdesk.entity.Ticket;
import com.trustdesk.service.TicketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.trustdesk.model.CreateTicketRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping
    public List<Ticket> getAllTickets() {
        return ticketService.getAllTickets();
    }

    @GetMapping("/{ticketId}")
    public ResponseEntity<?> getTicket(
            @PathVariable String ticketId) {

        return ticketService.getTicketById(ticketId)
                .map(ticket -> {

                    Map<String, Object> response =
                            new LinkedHashMap<>();

                    response.put("ticket", ticket);

                    Customer customer =
                            ticketService
                                    .getCustomerForTicket(ticket)
                                    .orElse(null);

                    Order order =
                            ticketService
                                    .getOrderForTicket(ticket)
                                    .orElse(null);

                    response.put("customer", customer);
                    response.put("order", order);

                    return ResponseEntity.ok(response);
                })
                .orElseGet(() ->
                        ResponseEntity.notFound().build());
    }
    @PostMapping
    public ResponseEntity<Ticket> createTicket(
            @Valid @RequestBody CreateTicketRequest request) {

        Ticket ticket = ticketService.createTicket(
                request.getCustomerId(),
                request.getOrderId(),
                request.getChannel(),
                request.getSubject(),
                request.getBody()
        );

        return ResponseEntity.ok(ticket);
    }
}