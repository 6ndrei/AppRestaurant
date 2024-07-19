package com.example.apprestaurant;

import static android.content.ContentValues.TAG;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apprestaurant.R;
import com.example.apprestaurant.adapters.CartAdapter;
import com.example.apprestaurant.SwipeToDeleteCallback;
import com.example.apprestaurant.models.CartModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class CartFragment extends Fragment {

    private List<CartModel> list;
    private CartAdapter cartAdapter;
    private RecyclerView recyclerView;
    private Button addToCartButton;
    private TextView totalPrice;
    private FirebaseFirestore firestore;
    private DocumentReference ordersRef;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();

    public CartFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_cart, container, false);

        recyclerView = view.findViewById(R.id.cart_rec);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        list = CartManager.getInstance().getCartList();
        cartAdapter = new CartAdapter(requireContext(), list);
        recyclerView.setAdapter(cartAdapter);

        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(requireContext()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                final CartModel deletedItem = list.get(position);
                list.remove(position);
                cartAdapter.notifyItemRemoved(position);
                double totalPriceValue = calculateTotalPrice();
                totalPrice.setText(String.valueOf(totalPriceValue));
                Snackbar snackbar = Snackbar
                        .make(recyclerView, "Produsul a fost sters din cos.", Snackbar.LENGTH_LONG)
                        .setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Undo deletion
                                list.add(position, deletedItem);
                                cartAdapter.notifyItemInserted(position);
                                recyclerView.scrollToPosition(position);
                                calculateAndShowTotalPrice();
                                double totalPriceValue = calculateTotalPrice();
                                totalPrice.setText(String.valueOf(totalPriceValue));
                            }
                        });
                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        totalPrice = view.findViewById(R.id.TotalPrice);
        calculateAndShowTotalPrice();

        addToCartButton = view.findViewById(R.id.AddToCart);
        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                syncCartWithFirestore();
                totalPrice.setText("0");
            }
        });

        return view;
    }

    private void calculateAndShowTotalPrice() {
        double totalPriceValue = calculateTotalPrice();
        totalPrice.setText(String.valueOf(totalPriceValue));
    }

    private double calculateTotalPrice() {
        double totalPrice = 0.0;
        for (CartModel item : list) {
            totalPrice += Double.parseDouble(item.getPrice());
        }
        return totalPrice;
    }

    private void syncCartWithFirestore() {
        if (list.isEmpty()) {
            return;
        }

        firestore = FirebaseFirestore.getInstance();
        ordersRef = firestore.collection("orders").document();

        double totalPriceValue = calculateTotalPrice();

        StringBuilder itemsNames = new StringBuilder();
        for (CartModel item : list) {
            itemsNames.append(item.getName()).append(", ");
        }
        String items = itemsNames.toString().trim();
        String tableNumber = list.get(0).getTableNumber(); // Access table number safely
        String status = "In Asteptare";
        String user = currentUser.getEmail();

        Order order = new Order(items, totalPriceValue, tableNumber, status, user);


        ordersRef.set(order)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Order added to Firestore");
                        CartManager.getInstance().clearCart();
                        cartAdapter.notifyDataSetChanged();
                        // Optionally, show a success message or perform another action
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error adding order to Firestore", e);
                        // Handle failure, show error message or retry logic
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        list = CartManager.getInstance().getCartList();
        cartAdapter.setList(list);
        cartAdapter.notifyDataSetChanged();
        calculateAndShowTotalPrice();
    }

    private static class Order {
        private String items;
        private double totalPrice;
        private String tableNumber;
        private String status;
        private String user;
        public Order() {
        }

        public Order(String items, double totalPrice, String tableNumber, String status, String user) {
            this.items = items;
            this.totalPrice = totalPrice;
            this.tableNumber = tableNumber;
            this.status = status;
            this.user = user;
        }

        public String getUser() { return user; }

        public void setUser(String user) { this.user = user; }

        public String getStatus() { return status; }

        public void setStatus(String status) { this.status = status; }

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
