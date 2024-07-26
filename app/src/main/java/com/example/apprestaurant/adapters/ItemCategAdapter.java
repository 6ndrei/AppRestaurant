package com.example.apprestaurant.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.apprestaurant.CartManager;
import com.example.apprestaurant.R;
import com.example.apprestaurant.models.CartModel;
import com.example.apprestaurant.models.ItemCategModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

public class ItemCategAdapter extends RecyclerView.Adapter<ItemCategAdapter.ViewHolder> {

    private BottomSheetDialog bottomSheetDialog;
    private final Context context;
    private final List<ItemCategModel> list;

    public ItemCategAdapter(Context context, List<ItemCategModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_categ_menu, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemCategModel item = list.get(position);

        Glide.with(context)
                .load(item.getImage())
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .centerCrop()
                .into(holder.imageView);

        holder.name.setText(item.getName());
        holder.price.setText(item.getPrice() + " RON");
        holder.timing.setText(item.getTiming() + " minute");

        holder.itemView.setOnClickListener(view -> showBottomSheetDialog(item));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private void showBottomSheetDialog(ItemCategModel item) {
        bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetTheme);

        View sheetView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_layout, null);
        Button minus = sheetView.findViewById(R.id.minusbutton);
        Button plus = sheetView.findViewById(R.id.plusbutton);
        TextView amountTextView = sheetView.findViewById(R.id.valOrder);

        final int[] amount = {1};
        amountTextView.setText(String.valueOf(amount[0]));

        minus.setOnClickListener(view -> {
            if (amount[0] > 1) {
                amount[0]--;
                amountTextView.setText(String.valueOf(amount[0]));
            }
        });

        plus.setOnClickListener(view -> {
            amount[0]++;
            amountTextView.setText(String.valueOf(amount[0]));
        });

        sheetView.findViewById(R.id.OrderAddLinear).setOnClickListener(view -> {
            SharedPreferences sharedPreferences = context.getSharedPreferences("Table_Session", Context.MODE_PRIVATE);
            String tableNumber = sharedPreferences.getString("qrContent", "");

            for (int i = 0; i < amount[0]; i++) {
                CartModel cartItem = new CartModel(item.getImage(), item.getName(), item.getPrice(), tableNumber);
                CartManager.getInstance().addToCart(cartItem);
            }

            Toast.makeText(context, "Adăugat în coș", Toast.LENGTH_SHORT).show();
            bottomSheetDialog.dismiss();
        });

        ImageView bottomImg = sheetView.findViewById(R.id.bottom_sheet_img);
        TextView bottomName = sheetView.findViewById(R.id.OrderName);
        TextView bottomPrice = sheetView.findViewById(R.id.OrderPrice);

        bottomName.setText(item.getName());
        bottomPrice.setText(item.getPrice());
        Glide.with(context)
                .load(item.getImage())
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .centerCrop()
                .into(bottomImg);

        bottomSheetDialog.setContentView(sheetView);
        bottomSheetDialog.show();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView imageView;
        final TextView name, timing, price;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_img);
            name = itemView.findViewById(R.id.item_name);
            price = itemView.findViewById(R.id.price);
            timing = itemView.findViewById(R.id.timing);
        }
    }
}
