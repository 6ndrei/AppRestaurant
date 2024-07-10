package com.example.apprestaurant.ui.dashboard;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private String tableNumber;
    private String username;
    private List<MenuItem> items;

    public Order(String tableNumber, String username) {
        this.tableNumber = tableNumber;
        this.username = username;
        this.items = new ArrayList<>();
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public String getUsername() {
        return username;
    }

    public List<MenuItem> getItems() {
        return items;
    }

    public void addItem(MenuItem item) {
        this.items.add(item);
    }

    // Nested class for menu items
    public static class MenuItem {
        private String itemName;
        private int quantity;

        public MenuItem(String itemName, int quantity) {
            this.itemName = itemName;
            this.quantity = quantity;
        }

        public String getItemName() {
            return itemName;
        }

        public int getQuantity() {
            return quantity;
        }
    }
}
