package com.example.apprestaurant;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apprestaurant.adapters.CartAdapter;
import com.example.apprestaurant.models.CartModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class CartFragment extends Fragment {

    List<CartModel> list;
    CartAdapter cartAdapter;
    RecyclerView recyclerView;
    Button addToCartButton;

    // Firestore
    FirebaseFirestore firestore;
    DocumentReference ordersRef;

    public CartFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_cart, container, false);

        recyclerView = view.findViewById(R.id.cart_rec);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        list = CartManager.getInstance().getCartList();
        cartAdapter = new CartAdapter(list);

        recyclerView.setAdapter(cartAdapter);

        addToCartButton = view.findViewById(R.id.AddToCart);
        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                syncCartWithFirestore();
            }
        });

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();
        ordersRef = firestore.collection("orders").document(); // Document ID will be auto-generated

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Update the local list and notify the adapter
        list = CartManager.getInstance().getCartList();
        cartAdapter = new CartAdapter(list);
        recyclerView.setAdapter(cartAdapter);
        cartAdapter.notifyDataSetChanged();
    }

    private void syncCartWithFirestore() {
        // Calculate total price
        double totalPrice = calculateTotalPrice();

        // Prepare data for Firestore
        StringBuilder itemsNames = new StringBuilder();
        for (CartModel item : list) {
            itemsNames.append(item.getName()).append(", ");
        }
        String items = itemsNames.toString().trim(); // Remove trailing comma and space

        // Get table number
        String tableNumber = list.get(0).getTableNumber(); // Assuming all items have the same table number

        // Create data object
        Order order = new Order(items, totalPrice, tableNumber);

        // Add order to Firestore
        ordersRef.set(order)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Data successfully written
                        Log.d(TAG, "Order added to Firestore");
                        // Clear the local cart list after order is successfully added
                        CartManager.getInstance().clearCart();
                        cartAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to write data
                        Log.e(TAG, "Error adding order to Firestore", e);
                    }
                });
    }

    private double calculateTotalPrice() {
        double totalPrice = 0.0;
        for (CartModel item : list) {
            totalPrice += Double.parseDouble(item.getPrice());
        }
        return totalPrice;
    }

    // Model class for Order
    private static class Order {
        private String items;
        private double totalPrice;
        private String tableNumber;

        public Order(String items, double totalPrice, String tableNumber) {
            this.items = items;
            this.totalPrice = totalPrice;
            this.tableNumber = tableNumber;
        }

        public String getItems() {
            return items;
        }

        public void setItems(String items) {
            this.items = items;
        }

        public double getTotalPrice() {
            return totalPrice;
        }

        public void setTotalPrice(double totalPrice) {
            this.totalPrice = totalPrice;
        }

        public String getTableNumber() {
            return tableNumber;
        }

        public void setTableNumber(String tableNumber) {
            this.tableNumber = tableNumber;
        }
    }
}
