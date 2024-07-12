package com.example.apprestaurant.models;

public class ItemCategModel {

    String image;
    String name;
    String timing;
    String price;

    public ItemCategModel(String image, String name, String timing, String price) {
        this.image = image;
        this.name = name;
        this.timing = timing;
        this.price = price;
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

    public String getTiming() {
        return timing;
    }

    public void setTiming(String timing) {
        this.timing = timing;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
