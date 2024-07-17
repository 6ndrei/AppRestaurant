package com.example.apprestaurant;

import com.example.apprestaurant.models.CartModel;

import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static CartManager instance;
    private List<CartModel> cartList;

    private CartManager() {
        cartList = new ArrayList<>();
    }

    public static CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public List<CartModel> getCartList() {
        return cartList;
    }

    public void addToCart(CartModel item) {
        cartList.add(item);
    }

    public void clearCart() {
        cartList.clear();
    }
}
