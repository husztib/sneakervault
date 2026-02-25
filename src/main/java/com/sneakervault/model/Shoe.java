package com.sneakervault.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Shoe {

    @Id
    private Long id;
    private String name;
    private String variant;
    private String color;
    private String styleCode;
    private String sizeUS;
    private Double sizeEUR;
    private String brand;
    private String type;
    private String gender;
    private Integer price;
    private Integer priceEUR;
    private Integer defaultPrice;
    private Integer defaultPriceEUR;
    private String imageUrl;
    private Boolean sold = false;
    private Boolean suspended = false;
    private Boolean onSale = false;
    private String saleType;
    private Integer salePercent;
    private Integer salePrice;
    private Integer salePriceEUR;

    @OneToMany(mappedBy = "shoe", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("displayOrder ASC")
    private List<ShoeImage> images = new ArrayList<>();

    public Shoe() {}

    public Shoe(Long id, String name, String variant, String color, String styleCode,
                String sizeUS, Double sizeEUR, String brand, String type, String gender,
                Integer price, Integer priceEUR, String imageUrl) {
        this.id = id;
        this.name = name;
        this.variant = variant;
        this.color = color;
        this.styleCode = styleCode;
        this.sizeUS = sizeUS;
        this.sizeEUR = sizeEUR;
        this.brand = brand;
        this.type = type;
        this.gender = gender;
        this.price = price;
        this.priceEUR = priceEUR;
        this.defaultPrice = price;
        this.defaultPriceEUR = priceEUR;
        this.imageUrl = imageUrl;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getVariant() { return variant; }
    public void setVariant(String variant) { this.variant = variant; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getStyleCode() { return styleCode; }
    public void setStyleCode(String styleCode) { this.styleCode = styleCode; }

    public String getSizeUS() { return sizeUS; }
    public void setSizeUS(String sizeUS) { this.sizeUS = sizeUS; }

    public Double getSizeEUR() { return sizeEUR; }
    public void setSizeEUR(Double sizeEUR) { this.sizeEUR = sizeEUR; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public Integer getPrice() { return price; }
    public void setPrice(Integer price) { this.price = price; }

    public Integer getPriceEUR() { return priceEUR; }
    public void setPriceEUR(Integer priceEUR) { this.priceEUR = priceEUR; }

    public Integer getDefaultPrice() { return defaultPrice; }
    public void setDefaultPrice(Integer defaultPrice) { this.defaultPrice = defaultPrice; }

    public Integer getDefaultPriceEUR() { return defaultPriceEUR; }
    public void setDefaultPriceEUR(Integer defaultPriceEUR) { this.defaultPriceEUR = defaultPriceEUR; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Boolean getSold() { return sold; }
    public void setSold(Boolean sold) { this.sold = sold; }

    public Boolean getSuspended() { return suspended; }
    public void setSuspended(Boolean suspended) { this.suspended = suspended; }

    public Boolean getOnSale() { return onSale; }
    public void setOnSale(Boolean onSale) { this.onSale = onSale; }

    public String getSaleType() { return saleType; }
    public void setSaleType(String saleType) { this.saleType = saleType; }

    public Integer getSalePercent() { return salePercent; }
    public void setSalePercent(Integer salePercent) { this.salePercent = salePercent; }

    public Integer getSalePrice() { return salePrice; }
    public void setSalePrice(Integer salePrice) { this.salePrice = salePrice; }

    public Integer getSalePriceEUR() { return salePriceEUR; }
    public void setSalePriceEUR(Integer salePriceEUR) { this.salePriceEUR = salePriceEUR; }

    public Integer getEffectivePrice() {
        if (!Boolean.TRUE.equals(onSale)) return price;
        if ("FIXED".equals(saleType) && salePrice != null) return salePrice;
        if ("PERCENTAGE".equals(saleType) && salePercent != null && price != null) {
            return (int) Math.round(price * (100.0 - salePercent) / 100.0);
        }
        return price;
    }

    public Integer getEffectivePriceEUR() {
        if (!Boolean.TRUE.equals(onSale)) return priceEUR;
        if ("FIXED".equals(saleType) && salePriceEUR != null) return salePriceEUR;
        if ("PERCENTAGE".equals(saleType) && salePercent != null && priceEUR != null) {
            return (int) Math.round(priceEUR * (100.0 - salePercent) / 100.0);
        }
        return priceEUR;
    }

    public List<ShoeImage> getImages() { return images; }
    public void setImages(List<ShoeImage> images) { this.images = images; }

    public String getPrimaryImageUrl() {
        return images.stream()
                .filter(img -> Boolean.TRUE.equals(img.getPrimaryImage()))
                .map(ShoeImage::getImageUrl)
                .findFirst()
                .orElse(imageUrl);
    }
}
