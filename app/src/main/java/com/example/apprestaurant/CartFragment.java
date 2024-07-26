package com.example.apprestaurant;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apprestaurant.adapters.CartAdapter;
import com.example.apprestaurant.models.CartModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.ListenerRegistration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

public class CartFragment extends Fragment {

    private static final String PREFS_NAME = "MyPrefs";
    private static final String KEY_ORDER_ACTIVE = "order_active";
    private static final String KEY_ORDER_ID = "order_id";

    private AnimationDrawable animationDrawable;
    private List<CartModel> list;
    private CartAdapter cartAdapter;
    private RecyclerView recyclerView;
    private Button addToCartButton;
    private TextView totalPrice;
    private TextView checkOrdersTextView;
    private FirebaseFirestore firestore;
    private CollectionReference ordersCollection;
    private ListenerRegistration orderStatusListener;
    private ConstraintLayout cartConstraint, cartConstraintt, orderConstraint;
    private SharedPreferences sharedPreferences;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();

    public CartFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_cart, container, false);
        firestore = FirebaseFirestore.getInstance();
        ordersCollection = firestore.collection("orders");
        sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        cartConstraint = view.findViewById(R.id.cartConstraint);
        cartConstraintt = view.findViewById(R.id.cartConstraintt);
        orderConstraint = view.findViewById(R.id.orderConstraint);

        animationDrawable = (AnimationDrawable) cartConstraint.getBackground();
        animationDrawable.setEnterFadeDuration(10);
        animationDrawable.setExitFadeDuration(2500);

        recyclerView = view.findViewById(R.id.cart_rec);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        list = CartManager.getInstance().getCartList();

        if (list.isEmpty()) {
            cartConstraintt.setVisibility(View.GONE);
            orderConstraint.setVisibility(View.VISIBLE);
        }
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
                if (totalPriceValue==0) {
                    cartConstraintt.setVisibility(View.GONE);
                    orderConstraint.setVisibility(View.VISIBLE);
                }
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
                                cartConstraintt.setVisibility(View.VISIBLE);
                                orderConstraint.setVisibility(View.GONE);
                            }
                        });
                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        totalPrice = view.findViewById(R.id.TotalPrice);
        checkOrdersTextView = view.findViewById(R.id.checkOrdersTextView);
        calculateAndShowTotalPrice();

        addToCartButton = view.findViewById(R.id.AddToCart);
        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferences = requireActivity().getSharedPreferences("Table_Session", 0);
                String qrContent = sharedPreferences.getString("qrContent", "");
                if (qrContent.equals("")) {
                    Toast.makeText(getActivity(), "Scaneaza mai intai codul QR de la masa", Toast.LENGTH_SHORT).show();
                return;
            }
                syncCartWithFirestore();
            }
        });

        checkOrdersTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showActiveOrdersDialog();
            }
        });
        boolean orderActive = sharedPreferences.getBoolean(KEY_ORDER_ACTIVE, false);
        if (orderActive) {
            String orderId = sharedPreferences.getString(KEY_ORDER_ID, null);
            if (orderId != null) {
                startOrderStatusListener(orderId);
            }
        }

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

        totalPrice.setText("0.0");
            cartConstraintt.setVisibility(View.GONE);
            orderConstraint.setVisibility(View.VISIBLE);

        DocumentReference newOrderRef = ordersCollection.document();

        double totalPriceValue = calculateTotalPrice();
        StringBuilder itemsNames = new StringBuilder();
        for (CartModel item : list) {
            itemsNames.append(item.getName()).append(", ");
        }
        sharedPreferences = requireActivity().getSharedPreferences("Table_Session", 0);
        String qrContent = sharedPreferences.getString("qrContent", "");
        String items = itemsNames.toString().trim();
        String tableNumber = qrContent;
        String status = "In Asteptare";
        String user = currentUser.getEmail();

        Order order = new Order(items, totalPriceValue, tableNumber, status, user);

        newOrderRef.set(order)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Order added to Firestore");
                        CartManager.getInstance().clearCart();
                        cartAdapter.notifyDataSetChanged();
                        showOrderPlacedBottomSheet(); // Afișează BottomSheet
                        saveOrderToPreferences(newOrderRef.getId(), status);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error adding order to Firestore", e);
                    }
                });
    }

    private void saveOrderToPreferences(String orderId, String status) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String existingOrders = sharedPreferences.getString("orders_list", "");
        String newOrder = orderId + "," + status + "," + System.currentTimeMillis();
        editor.putString("orders_list", existingOrders + newOrder + ";");
        editor.putBoolean(KEY_ORDER_ACTIVE, true);
        editor.putString(KEY_ORDER_ID, orderId);
        editor.apply();
    }

    private void showOrderPlacedBottomSheet() {
        SuccesBottomSheetFragment bottomSheet = SuccesBottomSheetFragment.newInstance("Comanda a fost plasată cu succes");
        bottomSheet.show(getChildFragmentManager(), bottomSheet.getTag());
    }

    private void showActiveOrdersDialog() {
        ordersCollection.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot snapshots) {
                if (snapshots != null && !snapshots.isEmpty()) {
                    StringBuilder message = new StringBuilder();
                    int count = 1;

                    for (DocumentSnapshot document : snapshots.getDocuments()) {
                        Order order = document.toObject(Order.class);
                        String user = order.getUser();
                        if (order != null && currentUser.getEmail().equals(user)) {
                            String orderId = document.getId();
                            String status = order.getStatus();
                            String time = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
                                    .format(new Date(order.getTimestamp()));
                            message.append("Comanda ").append(count++).append(" - ").append(time).append(" - ").append(status).append("\n");

                            // Update existing orders
                            saveOrderToPreferences(orderId, status);
                        }
                    }

                    // Creează și afișează dialogul
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    builder.setTitle("Comenzi Active");
                    builder.setMessage(message.toString());
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog activeOrdersDialog = builder.create();
                    activeOrdersDialog.show();
                } else {
                    // Nu sunt comenzi active, afișează Snackbar
                    Snackbar.make(requireView(), "Nu ai plasat nici o comandă", Snackbar.LENGTH_LONG)
                            .setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // Acțiune pentru butonul OK în Snackbar, dacă este necesar
                                }
                            })
                            .show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Error fetching orders", e);
                Snackbar.make(requireView(), "Eroare la încărcarea comenzilor.", Snackbar.LENGTH_LONG)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Acțiune pentru butonul OK în Snackbar, dacă este necesar
                            }
                        })
                        .show();
            }
        });
    }

    private void removeOrderFromPreferences(String orderId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String existingOrders = sharedPreferences.getString("orders_list", "");
        String[] orders = existingOrders.split(";");
        StringBuilder newOrders = new StringBuilder();
        for (String order : orders) {
            if (!order.isEmpty() && !order.startsWith(orderId)) {
                newOrders.append(order).append(";");
            }
        }
        editor.putString("orders_list", newOrders.toString());
        editor.apply();
    }

    private void clearAllOrdersFromPreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("orders_list");
        editor.apply();
    }

    private void startOrderStatusListener(String orderId) {
        if (orderStatusListener != null) {
            orderStatusListener.remove();
        }

        DocumentReference orderRef = ordersCollection.document(orderId);
        orderStatusListener = orderRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e(TAG, "Error listening to order status updates", e);
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    Order order = snapshot.toObject(Order.class);
                    if (order != null) {
                        String status = order.getStatus();
                        updateOrderInPreferences(orderId, status);
                    }
                } else {
                    // Order document was deleted
                    clearOrderIdFromPreferences();
                    removeOrderFromPreferences(orderId);
                }
            }
        });
    }

    private void updateOrderInPreferences(String orderId, String status) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String existingOrders = sharedPreferences.getString("orders_list", "");
        StringBuilder newOrders = new StringBuilder();
        String[] orders = existingOrders.split(";");
        for (String order : orders) {
            if (!order.isEmpty()) {
                String[] orderDetails = order.split(",");
                if (orderDetails[0].equals(orderId)) {
                    newOrders.append(orderId).append(",").append(status).append(",").append(orderDetails[2]).append(";");
                } else {
                    newOrders.append(order).append(";");
                }
            }
        }
        editor.putString("orders_list", newOrders.toString());
        editor.apply();
    }

    private void clearOrderIdFromPreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_ORDER_ID);
        editor.remove(KEY_ORDER_ACTIVE);
        editor.apply();
    }

    private static class Order {
        private String items;
        private double totalPrice;
        private String tableNumber;
        private String status;
        private String user;
        private long timestamp; // Add a timestamp field

        public Order() {}

        public Order(String items, double totalPrice, String tableNumber, String status, String user) {
            this.items = items;
            this.totalPrice = totalPrice;
            this.tableNumber = tableNumber;
            this.status = status;
            this.user = user;
            this.timestamp = System.currentTimeMillis(); // Set timestamp when creating the order
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

        public long getTimestamp() { return timestamp; }

        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Pornește animația
        if (animationDrawable != null && !animationDrawable.isRunning()) {
            animationDrawable.start();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Oprește animația
        if (animationDrawable != null && animationDrawable.isRunning()) {
            animationDrawable.stop();
        }
    }
}
