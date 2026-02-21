package com.sneakervault.dto;

public class PriceUpdateRequest {
    private Integer price;
    private Integer priceEUR;

    public Integer getPrice() { return price; }
    public void setPrice(Integer price) { this.price = price; }

    public Integer getPriceEUR() { return priceEUR; }
    public void setPriceEUR(Integer priceEUR) { this.priceEUR = priceEUR; }
}
