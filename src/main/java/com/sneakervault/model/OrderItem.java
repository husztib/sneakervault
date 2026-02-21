package com.sneakervault.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    @JsonIgnore
    private ShoeOrder order;

    private Long shoeId;
    private String name;
    private String color;
    private String styleCode;
    private Double sizeEUR;
    private String sizeUS;
    private Integer price;
    private Integer priceEUR;
    private String imageUrl;

    public OrderItem() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ShoeOrder getOrder() { return order; }
    public void setOrder(ShoeOrder order) { this.order = order; }

    public Long getShoeId() { return shoeId; }
    public void setShoeId(Long shoeId) { this.shoeId = shoeId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getStyleCode() { return styleCode; }
    public void setStyleCode(String styleCode) { this.styleCode = styleCode; }

    public Double getSizeEUR() { return sizeEUR; }
    public void setSizeEUR(Double sizeEUR) { this.sizeEUR = sizeEUR; }

    public String getSizeUS() { return sizeUS; }
    public void setSizeUS(String sizeUS) { this.sizeUS = sizeUS; }

    public Integer getPrice() { return price; }
    public void setPrice(Integer price) { this.price = price; }

    public Integer getPriceEUR() { return priceEUR; }
    public void setPriceEUR(Integer priceEUR) { this.priceEUR = priceEUR; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
