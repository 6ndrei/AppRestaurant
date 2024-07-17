package com.example.apprestaurant.models;

public class CartModel {
    private String image;
    private String name;
    private String price;
    private String tableNumber;

    public CartModel() {
        // Required empty public constructor for Firestore
    }

    public CartModel(String image, String name, String price, String tableNumber) {
        this.image = image;
        this.name = name;
        this.price = price;
        this.tableNumber = tableNumber;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }
}
