package com.trustdesk.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @JsonProperty("order_id")
    @Column(name = "order_id", nullable = false, length = 50)
    private String orderId;

    @JsonProperty("customer_id")
    @Column(name = "customer_id", nullable = false, length = 50)
    private String customerId;

    @Column(nullable = false, length = 30)
    private String status;

    @JsonProperty("placed_at")
    @Column(name = "placed_at", nullable = false)
    private LocalDate placedAt;

    @JsonProperty("delivered_at")
    @Column(name = "delivered_at")
    private LocalDate deliveredAt;

    @JsonProperty("eligible_return_until")
    @Column(name = "eligible_return_until")
    private LocalDate eligibleReturnUntil;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @Column(nullable = false, length = 10)
    private String currency;

    @JsonProperty("payment_status")
    @Column(name = "payment_status", nullable = false, length = 30)
    private String paymentStatus;

    @JsonProperty("tracking_number")
    @Column(name = "tracking_number", length = 100)
    private String trackingNumber;

    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<OrderItem> items = new ArrayList<>();

    public Order() {
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getPlacedAt() {
        return placedAt;
    }

    public void setPlacedAt(LocalDate placedAt) {
        this.placedAt = placedAt;
    }

    public LocalDate getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(LocalDate deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public LocalDate getEligibleReturnUntil() {
        return eligibleReturnUntil;
    }

    public void setEligibleReturnUntil(LocalDate eligibleReturnUntil) {
        this.eligibleReturnUntil = eligibleReturnUntil;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }
}