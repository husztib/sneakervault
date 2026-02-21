package com.sneakervault.dto;

import java.util.List;

public class CheckoutRequest {
    private String currency;
    private List<CheckoutItemRequest> items;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String customerZip;
    private String customerCity;
    private String customerStreet;
    private String customerNotes;

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public List<CheckoutItemRequest> getItems() { return items; }
    public void setItems(List<CheckoutItemRequest> items) { this.items = items; }

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
}
