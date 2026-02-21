package com.sneakervault.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
public class ShoeImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shoe_id")
    @JsonIgnore
    private Shoe shoe;

    private String imageUrl;
    private Integer displayOrder;
    private Boolean primaryImage;

    public ShoeImage() {}

    public ShoeImage(Shoe shoe, String imageUrl, Integer displayOrder, Boolean primaryImage) {
        this.shoe = shoe;
        this.imageUrl = imageUrl;
        this.displayOrder = displayOrder;
        this.primaryImage = primaryImage;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Shoe getShoe() { return shoe; }
    public void setShoe(Shoe shoe) { this.shoe = shoe; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }

    public Boolean getPrimaryImage() { return primaryImage; }
    public void setPrimaryImage(Boolean primaryImage) { this.primaryImage = primaryImage; }
}
