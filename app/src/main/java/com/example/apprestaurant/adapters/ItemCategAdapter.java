package com.example.apprestaurant.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;

public class ItemCategAdapter extends RecyclerView.Adapter<ItemCategAdapter.ViewHolder> {

    private BottomSheetDialog bottomSheetDialog;
    Context context;
    ArrayList<ItemCategModel> list;

    public ItemCategAdapter(Context context, ArrayList<ItemCategModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_categ_menu, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Use Glide to load the image from URL
        Glide.with(context)
                .load(list.get(position).getImage()) // Assuming getImage() returns a URL String
                .placeholder(R.drawable.placeholder_image) // Placeholder image while loading
                .error(R.drawable.error_image) // Error image if Glide fails to load
                .centerCrop()
                .into(holder.imageView);

        final String mName = list.get(position).getName();
        final String mPrice = list.get(position).getPrice();
        final String mImage = list.get(position).getImage();

        holder.name.setText(list.get(position).getName());
        holder.price.setText(list.get(position).getPrice());
        holder.timing.setText(list.get(position).getTiming());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetTheme);

                View sheetView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_layout, null);
                sheetView.findViewById(R.id.OrderAddLinear).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SharedPreferences sharedPreferences = context.getSharedPreferences("Table_Session", Context.MODE_PRIVATE);
                        String tableNumber = sharedPreferences.getString("qrContent", ""); // Obține numărul mesei din sharedPreferences

                        CartModel item = new CartModel(mImage, mName, mPrice, tableNumber);
                        CartManager.getInstance().addToCart(item);
                        Toast.makeText(context, "Adăugat în coș", Toast.LENGTH_SHORT).show();
                        bottomSheetDialog.dismiss();
                    }
                });

                ImageView bottomImg = sheetView.findViewById(R.id.bottom_sheet_img);
                TextView bottomName = sheetView.findViewById(R.id.OrderName);
                TextView bottomPrice = sheetView.findViewById(R.id.OrderPrice);

                bottomName.setText(mName);
                bottomPrice.setText(mPrice);
                Glide.with(context)
                        .load(mImage) // Assuming mImage is a URL string
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.error_image)
                        .centerCrop()
                        .into(bottomImg);

                bottomSheetDialog.setContentView(sheetView);
                bottomSheetDialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView name, timing, price;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_img);
            name = itemView.findViewById(R.id.item_name);
            price = itemView.findViewById(R.id.price);
            timing = itemView.findViewById(R.id.timing);
        }
    }
}
