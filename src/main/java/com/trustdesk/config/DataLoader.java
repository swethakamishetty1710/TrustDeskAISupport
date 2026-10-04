package com.trustdesk.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trustdesk.entity.Customer;
import com.trustdesk.entity.Order;
import com.trustdesk.entity.OrderItem;
import com.trustdesk.entity.Ticket;
import com.trustdesk.repository.CustomerRepository;
import com.trustdesk.repository.OrderRepository;
import com.trustdesk.repository.TicketRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final TicketRepository ticketRepository;
    private final ObjectMapper objectMapper;

    public DataLoader(
            CustomerRepository customerRepository,
            OrderRepository orderRepository,
            TicketRepository ticketRepository,
            ObjectMapper objectMapper) {

        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
        this.ticketRepository = ticketRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run(String... args) throws Exception {

        loadCustomers();
        loadOrders();
        loadTickets();

        System.out.println("========================================");
        System.out.println("TrustDesk data loading completed");
        System.out.println("========================================");
    }

    private void loadCustomers() throws Exception {

        File file = new File("data/customers.json");

        if (!file.exists()) {
            System.out.println("customers.json not found at: "
                    + file.getAbsolutePath());
            return;
        }

        List<Customer> customers = objectMapper.readValue(
                file,
                new TypeReference<List<Customer>>() {
                }
        );

        int inserted = 0;
        int skipped = 0;

        for (Customer customer : customers) {

            if (customerRepository.existsById(customer.getCustomerId())) {
                skipped++;
                continue;
            }

            customerRepository.save(customer);
            inserted++;
        }

        System.out.println("----------------------------------------");
        System.out.println("Customer Data");
        System.out.println("Customers in JSON : " + customers.size());
        System.out.println("Customers inserted: " + inserted);
        System.out.println("Customers skipped  : " + skipped);
        System.out.println("----------------------------------------");
    }

    private void loadOrders() throws Exception {

        File file = new File("data/orders.json");

        if (!file.exists()) {
            System.out.println("orders.json not found at: "
                    + file.getAbsolutePath());
            return;
        }

        List<Order> orders = objectMapper.readValue(
                file,
                new TypeReference<List<Order>>() {
                }
        );

        int ordersInserted = 0;
        int ordersSkipped = 0;
        int itemsInserted = 0;

        for (Order order : orders) {

            if (orderRepository.existsById(order.getOrderId())) {
                ordersSkipped++;
                continue;
            }

            for (OrderItem item : order.getItems()) {
                item.setOrder(order);
            }

            orderRepository.save(order);

            ordersInserted++;
            itemsInserted += order.getItems().size();
        }

        System.out.println("----------------------------------------");
        System.out.println("Order Data");
        System.out.println("Orders in JSON   : " + orders.size());
        System.out.println("Orders inserted  : " + ordersInserted);
        System.out.println("Orders skipped    : " + ordersSkipped);
        System.out.println("Items inserted    : " + itemsInserted);
        System.out.println("----------------------------------------");
    }

    private void loadTickets() throws Exception {

        File file = new File("data/tickets.json");

        if (!file.exists()) {
            System.out.println("tickets.json not found at: "
                    + file.getAbsolutePath());
            return;
        }

        List<Ticket> tickets = objectMapper.readValue(
                file,
                new TypeReference<List<Ticket>>() {
                }
        );

        int inserted = 0;
        int skipped = 0;

        for (Ticket ticket : tickets) {

            if (ticketRepository.existsById(ticket.getTicketId())) {
                skipped++;
                continue;
            }

            ticketRepository.save(ticket);
            inserted++;
        }

        System.out.println("----------------------------------------");
        System.out.println("Ticket Data");
        System.out.println("Tickets in JSON : " + tickets.size());
        System.out.println("Tickets inserted: " + inserted);
        System.out.println("Tickets skipped  : " + skipped);
        System.out.println("----------------------------------------");
    }
}