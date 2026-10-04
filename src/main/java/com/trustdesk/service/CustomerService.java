package com.trustdesk.service;

import com.trustdesk.entity.Customer;
import com.trustdesk.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Optional<Customer> getCustomerById(String customerId) {
        return customerRepository.findById(customerId);
    }

    public Customer save(Customer customer) {
        return customerRepository.save(customer);
    }

    public void delete(String customerId) {
        customerRepository.deleteById(customerId);
    }
}