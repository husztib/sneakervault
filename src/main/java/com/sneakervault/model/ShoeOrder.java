package com.sneakervault.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class ShoeOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime orderDate;
    private String currency;

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PENDING;
    private Integer totalHUF;
    private Integer totalEUR;

    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String customerZip;
    private String customerCity;
    private String customerStreet;

    @Column(length = 1000)
    private String customerNotes;

    private String language;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    public ShoeOrder() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public Integer getTotalHUF() { return totalHUF; }
    public void setTotalHUF(Integer totalHUF) { this.totalHUF = totalHUF; }

    public Integer getTotalEUR() { return totalEUR; }
    public void setTotalEUR(Integer totalEUR) { this.totalEUR = totalEUR; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public String getCustomerZip() { return customerZip; }
    public void setCustomerZip(String customerZip) { this.customerZip = customerZip; }

    public String getCustomerCity() { return customerCity; }
    public void setCustomerCity(String customerCity) { this.customerCity = customerCity; }

    public String getCustomerStreet() { return customerStreet; }
    public void setCustomerStreet(String customerStreet) { this.customerStreet = customerStreet; }

    public String getCustomerNotes() { return customerNotes; }
    public void setCustomerNotes(String customerNotes) { this.customerNotes = customerNotes; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
}
