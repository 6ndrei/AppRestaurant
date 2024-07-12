package com.example.apprestaurant.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.apprestaurant.R;
import com.example.apprestaurant.models.ItemCategModel;

import java.util.ArrayList;

public class ItemCategAdapter extends RecyclerView.Adapter<ItemCategAdapter.ViewHolder> {

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
                .into(holder.imageView);

        holder.name.setText(list.get(position).getName());
        holder.price.setText(list.get(position).getPrice());
        holder.timing.setText(list.get(position).getTiming());
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
