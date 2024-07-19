package com.example.apprestaurant;

import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apprestaurant.adapters.OrdersAdapter;
import com.example.apprestaurant.models.OrderModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class OrdersActivity extends AppCompatActivity {

    private static final String TAG = "OrdersActivity";

    List<OrderModel> ordersList;
    OrdersAdapter ordersAdapter;
    RecyclerView recyclerView;

    FirebaseFirestore firestore;

    private static final String[] STATUS = {"In Asteptare", "Preluata", "Finalizata"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        recyclerView = findViewById(R.id.orders_rec);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ordersList = new ArrayList<>();
        ordersAdapter = new OrdersAdapter(ordersList);

        recyclerView.setAdapter(ordersAdapter);

        firestore = FirebaseFirestore.getInstance();

        listenForOrders();
    }

    private void listenForOrders() {
        firestore.collection("orders")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e(TAG, "Listen failed.", error);
                            return;
                        }

                        ordersList.clear();
                        if (value != null) {
                            for (QueryDocumentSnapshot doc : value) {
                                try {
                                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                                    FirebaseUser currentUser = mAuth.getCurrentUser();
                                    if (currentUser != null) {
                                        String uid = currentUser.getUid();
                                        String email = currentUser.getEmail(); // Obține email-ul
                                        OrderModel order = doc.toObject(OrderModel.class);
                                        order.setId(doc.getId());
                                        order.setUserId(uid);
                                        order.setUserEmail(email); // Setează email-ul
                                        ordersList.add(order);
                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, "Error parsing order", e);
                                }
                            }
                            ordersAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }


}
