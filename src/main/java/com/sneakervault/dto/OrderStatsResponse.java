package com.sneakervault.dto;

public class OrderStatsResponse {
    private long totalOrders;
    private long totalItemsSold;
    private long totalHUF;
    private long totalEUR;

    public OrderStatsResponse(long totalOrders, long totalItemsSold, long totalHUF, long totalEUR) {
        this.totalOrders = totalOrders;
        this.totalItemsSold = totalItemsSold;
        this.totalHUF = totalHUF;
        this.totalEUR = totalEUR;
    }

    public long getTotalOrders() { return totalOrders; }
    public long getTotalItemsSold() { return totalItemsSold; }
    public long getTotalHUF() { return totalHUF; }
    public long getTotalEUR() { return totalEUR; }
}
