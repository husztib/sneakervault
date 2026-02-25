package com.sneakervault.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "shoe_clicks")
public class ShoeClick {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long shoeId;
    private String sessionId;
    private LocalDateTime clickedAt;

    public ShoeClick() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getShoeId() { return shoeId; }
    public void setShoeId(Long shoeId) { this.shoeId = shoeId; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public LocalDateTime getClickedAt() { return clickedAt; }
    public void setClickedAt(LocalDateTime clickedAt) { this.clickedAt = clickedAt; }
}
