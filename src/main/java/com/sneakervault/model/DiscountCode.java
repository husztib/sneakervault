package com.sneakervault.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class DiscountCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private String type; // "PERCENTAGE" or "FIXED"

    private Integer percentOff;
    private Integer fixedAmountHUF;
    private Integer fixedAmountEUR;
    private LocalDateTime expiresAt;
    private Integer maxUses;

    @Column(columnDefinition = "integer default 0")
    private Integer usedCount = 0;

    @Column(columnDefinition = "boolean default true")
    private Boolean active = true;

    public DiscountCode() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Integer getPercentOff() { return percentOff; }
    public void setPercentOff(Integer percentOff) { this.percentOff = percentOff; }

    public Integer getFixedAmountHUF() { return fixedAmountHUF; }
    public void setFixedAmountHUF(Integer fixedAmountHUF) { this.fixedAmountHUF = fixedAmountHUF; }

    public Integer getFixedAmountEUR() { return fixedAmountEUR; }
    public void setFixedAmountEUR(Integer fixedAmountEUR) { this.fixedAmountEUR = fixedAmountEUR; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public Integer getMaxUses() { return maxUses; }
    public void setMaxUses(Integer maxUses) { this.maxUses = maxUses; }

    public Integer getUsedCount() { return usedCount; }
    public void setUsedCount(Integer usedCount) { this.usedCount = usedCount; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
