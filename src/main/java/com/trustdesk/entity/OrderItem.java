package com.trustdesk.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("sku")
    @Column(nullable = false, length = 100)
    private String sku;

    @JsonProperty("name")
    @Column(nullable = false, length = 200)
    private String name;

    @JsonProperty("quantity")
    @Column(nullable = false)
    private Integer quantity;

    @JsonProperty("category")
    @Column(nullable = false, length = 100)
    private String category;

    @JsonProperty("final_sale")
    @Column(name = "final_sale", nullable = false)
    private boolean finalSale;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnore
    private Order order;

    public OrderItem() {
    }

    public Long getId() {
        return id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isFinalSale() {
        return finalSale;
    }

    public void setFinalSale(boolean finalSale) {
        this.finalSale = finalSale;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}