package com.sneakervault.model;

import jakarta.persistence.*;

@Entity
public class StoreSettings {

    @Id
    private Long id = 1L;

    @Column(columnDefinition = "boolean default false")
    private Boolean storeLive = false;

    // Shipping method enabled flags
    @Column(columnDefinition = "boolean default true")
    private Boolean shippingMagyarPostaEnabled = true;

    @Column(columnDefinition = "boolean default true")
    private Boolean shippingGlsEnabled = true;

    @Column(columnDefinition = "boolean default true")
    private Boolean shippingDpdEnabled = true;

    @Column(columnDefinition = "boolean default true")
    private Boolean shippingCsomagpontEnabled = true;

    // Shipping costs HUF
    @Column(columnDefinition = "integer default 1490")
    private Integer shippingMagyarPostaHuf = 1490;

    @Column(columnDefinition = "integer default 1990")
    private Integer shippingGlsHuf = 1990;

    @Column(columnDefinition = "integer default 1990")
    private Integer shippingDpdHuf = 1990;

    @Column(columnDefinition = "integer default 990")
    private Integer shippingCsomagpontHuf = 990;

    // Shipping costs EUR
    @Column(columnDefinition = "integer default 4")
    private Integer shippingMagyarPostaEur = 4;

    @Column(columnDefinition = "integer default 5")
    private Integer shippingGlsEur = 5;

    @Column(columnDefinition = "integer default 5")
    private Integer shippingDpdEur = 5;

    @Column(columnDefinition = "integer default 3")
    private Integer shippingCsomagpontEur = 3;

    // International shipping
    @Column(columnDefinition = "boolean default true")
    private Boolean internationalShippingEnabled = true;

    public StoreSettings() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Boolean getStoreLive() { return storeLive; }
    public void setStoreLive(Boolean storeLive) { this.storeLive = storeLive; }

    public Boolean getShippingMagyarPostaEnabled() { return shippingMagyarPostaEnabled; }
    public void setShippingMagyarPostaEnabled(Boolean v) { this.shippingMagyarPostaEnabled = v; }

    public Boolean getShippingGlsEnabled() { return shippingGlsEnabled; }
    public void setShippingGlsEnabled(Boolean v) { this.shippingGlsEnabled = v; }

    public Boolean getShippingDpdEnabled() { return shippingDpdEnabled; }
    public void setShippingDpdEnabled(Boolean v) { this.shippingDpdEnabled = v; }

    public Boolean getShippingCsomagpontEnabled() { return shippingCsomagpontEnabled; }
    public void setShippingCsomagpontEnabled(Boolean v) { this.shippingCsomagpontEnabled = v; }

    public Integer getShippingMagyarPostaHuf() { return shippingMagyarPostaHuf; }
    public void setShippingMagyarPostaHuf(Integer v) { this.shippingMagyarPostaHuf = v; }

    public Integer getShippingGlsHuf() { return shippingGlsHuf; }
    public void setShippingGlsHuf(Integer v) { this.shippingGlsHuf = v; }

    public Integer getShippingDpdHuf() { return shippingDpdHuf; }
    public void setShippingDpdHuf(Integer v) { this.shippingDpdHuf = v; }

    public Integer getShippingCsomagpontHuf() { return shippingCsomagpontHuf; }
    public void setShippingCsomagpontHuf(Integer v) { this.shippingCsomagpontHuf = v; }

    public Integer getShippingMagyarPostaEur() { return shippingMagyarPostaEur; }
    public void setShippingMagyarPostaEur(Integer v) { this.shippingMagyarPostaEur = v; }

    public Integer getShippingGlsEur() { return shippingGlsEur; }
    public void setShippingGlsEur(Integer v) { this.shippingGlsEur = v; }

    public Integer getShippingDpdEur() { return shippingDpdEur; }
    public void setShippingDpdEur(Integer v) { this.shippingDpdEur = v; }

    public Integer getShippingCsomagpontEur() { return shippingCsomagpontEur; }
    public void setShippingCsomagpontEur(Integer v) { this.shippingCsomagpontEur = v; }

    public Boolean getInternationalShippingEnabled() { return internationalShippingEnabled; }
    public void setInternationalShippingEnabled(Boolean v) { this.internationalShippingEnabled = v; }
}
