package com.trustdesk.repository;

import com.trustdesk.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, String> {

    List<Order> findByCustomerId(String customerId);
}