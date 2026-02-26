package com.sneakervault.model;

import jakarta.persistence.*;

@Entity
public class StoreSettings {

    @Id
    private Long id = 1L;

    @Column(columnDefinition = "boolean default false")
    private Boolean storeLive = false;

    public StoreSettings() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Boolean getStoreLive() { return storeLive; }
    public void setStoreLive(Boolean storeLive) { this.storeLive = storeLive; }
}
