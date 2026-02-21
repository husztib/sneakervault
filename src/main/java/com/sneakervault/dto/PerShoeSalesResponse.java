package com.sneakervault.dto;

public class PerShoeSalesResponse {
    private Long shoeId;
    private String name;
    private String color;
    private String styleCode;
    private int timesSold;
    private long totalHUF;
    private long totalEUR;
    private String lastDate;

    public Long getShoeId() { return shoeId; }
    public void setShoeId(Long shoeId) { this.shoeId = shoeId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getStyleCode() { return styleCode; }
    public void setStyleCode(String styleCode) { this.styleCode = styleCode; }

    public int getTimesSold() { return timesSold; }
    public void setTimesSold(int timesSold) { this.timesSold = timesSold; }

    public long getTotalHUF() { return totalHUF; }
    public void setTotalHUF(long totalHUF) { this.totalHUF = totalHUF; }

    public long getTotalEUR() { return totalEUR; }
    public void setTotalEUR(long totalEUR) { this.totalEUR = totalEUR; }

    public String getLastDate() { return lastDate; }
    public void setLastDate(String lastDate) { this.lastDate = lastDate; }
}
