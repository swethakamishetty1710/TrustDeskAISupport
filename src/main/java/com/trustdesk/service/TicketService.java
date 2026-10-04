package com.trustdesk.service;

import com.trustdesk.entity.Customer;
import com.trustdesk.entity.Order;
import com.trustdesk.entity.Ticket;
import com.trustdesk.repository.CustomerRepository;
import com.trustdesk.repository.OrderRepository;
import com.trustdesk.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;

    public TicketService(
            TicketRepository ticketRepository,
            CustomerRepository customerRepository,
            OrderRepository orderRepository) {

        this.ticketRepository = ticketRepository;
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
    }

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public Optional<Ticket> getTicketById(String ticketId) {
        return ticketRepository.findById(ticketId);
    }

    public Optional<Customer> getCustomerForTicket(Ticket ticket) {
        return customerRepository.findById(ticket.getCustomerId());
    }

    public Optional<Order> getOrderForTicket(Ticket ticket) {

        if (ticket.getOrderId() == null ||
                ticket.getOrderId().isBlank()) {
            return Optional.empty();
        }

        return orderRepository.findById(ticket.getOrderId());
    }
    public Ticket createTicket(
            String customerId,
            String orderId,
            String channel,
            String subject,
            String body) {

        Ticket ticket = new Ticket();

        ticket.setTicketId(generateTicketId());
        ticket.setCustomerId(customerId);
        ticket.setOrderId(orderId);
        ticket.setChannel(channel);
        ticket.setSubject(subject);
        ticket.setBody(body);

        ticket.setCreatedAt(java.time.OffsetDateTime.now());
        ticket.setStatus("open");

        return ticketRepository.save(ticket);
    }

    private String generateTicketId() {

        long count = ticketRepository.count() + 9001;

        String ticketId = "tkt_" + count;

        while (ticketRepository.existsById(ticketId)) {
            count++;
            ticketId = "tkt_" + count;
        }

        return ticketId;
    }
}