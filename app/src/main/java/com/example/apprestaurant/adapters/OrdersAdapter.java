package com.example.apprestaurant.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apprestaurant.R;
import com.example.apprestaurant.models.OrderModel;

import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrderViewHolder> {

    List<OrderModel> ordersList;

    public OrdersAdapter(List<OrderModel> ordersList) {
        this.ordersList = ordersList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderModel order = ordersList.get(position);
        holder.itemsTextView.setText(order.getItems());
        holder.tableNumberTextView.setText(order.getTableNumber());
        holder.totalPriceTextView.setText(String.valueOf(order.getTotalPrice()));
    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView itemsTextView;
        TextView tableNumberTextView;
        TextView totalPriceTextView;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            itemsTextView = itemView.findViewById(R.id.items);
            tableNumberTextView = itemView.findViewById(R.id.table_number);
            totalPriceTextView = itemView.findViewById(R.id.total_price);
        }
    }
}
