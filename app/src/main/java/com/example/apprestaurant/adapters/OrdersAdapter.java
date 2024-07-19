package com.example.apprestaurant.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apprestaurant.R;
import com.example.apprestaurant.models.OrderModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrderViewHolder> {

    List<OrderModel> ordersList;
    FirebaseFirestore firestore;
    private FirebaseAuth mAuth;
    private static final String[] STATUS = {"In Asteptare", "Preluata", "Finalizata"};

    public OrdersAdapter(List<OrderModel> ordersList) {
        this.ordersList = ordersList;
        this.firestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String uid = currentUser.getUid();
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderModel order = ordersList.get(position);
        holder.itemsTextView.setText(order.getItems());
        holder.tableNumberTextView.setText(order.getTableNumber());
        holder.totalPriceTextView.setText(String.valueOf(order.getTotalPrice()));
        holder.status.setText(order.getStatus());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(holder.itemView.getContext(), android.R.layout.simple_spinner_item, STATUS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.statusSpinner.setAdapter(adapter);

        holder.updateButton.setOnClickListener(v -> {
            String selectedStatus = holder.statusSpinner.getSelectedItem().toString();
            if ("Finalizata".equals(selectedStatus)) {
                moveToHistory(order);
            } else {
                updateOrderStatus(order, selectedStatus);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }

    private void updateOrderStatus(OrderModel order, String status) {
        firestore.collection("orders").document(order.getId())
                .update("status", status)
                .addOnSuccessListener(aVoid -> {
                    // Actualizare reușită
                })
                .addOnFailureListener(e -> {
                    // Eroare la actualizare
                });
    }

    private void moveToHistory(OrderModel order) {
        String orderId = order.getId();
        String userEmail = order.getUserEmail();
        if (orderId == null || userEmail == null) {
            Log.e("OrdersAdapter", "Order ID or User Email is null. Cannot move to history.");
            return;
        }

        firestore.collection("orders_history").document(orderId).set(order)
                .addOnSuccessListener(aVoid -> {
                    firestore.collection("orders").document(orderId).delete()
                            .addOnSuccessListener(aVoid1 -> {
                                ordersList.remove(order);
                                notifyDataSetChanged();
                                updateUserOrderHistory(userEmail, orderId);
                            })
                            .addOnFailureListener(e -> {
                                // Eroare la ștergerea comenzii
                            });
                })
                .addOnFailureListener(e -> {
                    // Eroare la mutarea comenzii în istoric
                });
    }

    private void updateUserOrderHistory(String userEmail, String orderId) {
        if (userEmail == null || orderId == null) {
            Log.e("OrdersAdapter", "User Email or Order ID is null. Cannot update user order history.");
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").whereEqualTo("email", userEmail).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            db.collection("users").document(document.getId())
                                    .update("orders_history", FieldValue.arrayUnion(orderId))
                                    .addOnSuccessListener(aVoid -> {
                                        // Actualizare reușită a orders_history pentru utilizator
                                    })
                                    .addOnFailureListener(e -> {
                                        // Eroare la actualizarea orders_history
                                    });
                        }
                    } else {
                        Log.e("OrdersAdapter", "Error getting user document.", task.getException());
                    }
                });
    }


    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView itemsTextView;
        TextView tableNumberTextView;
        TextView totalPriceTextView;
        Spinner statusSpinner;
        Button updateButton;
        TextView status;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            itemsTextView = itemView.findViewById(R.id.items);
            tableNumberTextView = itemView.findViewById(R.id.table_number);
            totalPriceTextView = itemView.findViewById(R.id.total_price);
            statusSpinner = itemView.findViewById(R.id.UpdateOrderSpinner);
            updateButton = itemView.findViewById(R.id.UpdateOrder);
            status = itemView.findViewById(R.id.OrderStatus);
        }
    }
}
