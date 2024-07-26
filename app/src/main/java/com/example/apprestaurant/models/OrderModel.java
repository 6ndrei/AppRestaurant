package com.example.apprestaurant.models;

public class OrderModel {
    private String id;
    private String items;
    private String tableNumber;
    private double totalPrice;
    private String userId;
    private String status;
    private String userEmail;
    private Long timestamp;

    public OrderModel() {
        // Constructor fără argumente necesar pentru Firestore
    }

    public OrderModel(String id, String items, String tableNumber, double totalPrice, String userId, String status, Long timestamp) {
        this.id = id;
        this.items = items;
        this.tableNumber = tableNumber;
        this.totalPrice = totalPrice;
        this.userId = userId;
        this.status = status;
        this.timestamp = timestamp;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }


}
