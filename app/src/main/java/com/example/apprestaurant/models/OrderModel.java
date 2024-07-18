package com.example.apprestaurant.models;

public class OrderModel {
    private String items;
    private String tableNumber;
    private double totalPrice;

    public OrderModel() {
        // Constructor fără argumente necesar pentru Firestore
    }

    public OrderModel(String items, String tableNumber, double totalPrice) {
        this.items = items;
        this.tableNumber = tableNumber;
        this.totalPrice = totalPrice;
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
