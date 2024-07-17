package com.example.apprestaurant.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.apprestaurant.R;
import com.example.apprestaurant.models.CategModel;
import com.example.apprestaurant.models.UpdateItemCateg;

import java.util.ArrayList;

public class CategAdapter extends RecyclerView.Adapter<CategAdapter.ViewHolder> {

    private final UpdateItemCateg updateItemCateg;
    private final Activity activity;
    private final ArrayList<CategModel> list;

    private int row_index = 0;

    public CategAdapter(Activity activity, ArrayList<CategModel> list, UpdateItemCateg updateItemCateg) {
        this.activity = activity;
        this.list = list;
        this.updateItemCateg = updateItemCateg;

        if (!list.isEmpty()) {
            updateItemCateg.callBack(0, new ArrayList<>());
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_categories, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategModel categModel = list.get(position);

        Glide.with(activity)
                .load(categModel.getImage())
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .centerCrop()
                .into(holder.imageView);

        holder.name.setText(categModel.getName());

        if (position == row_index) {
            holder.cardViewItems.setBackgroundResource(R.drawable.change_bg);
        } else {
            holder.cardViewItems.setBackgroundResource(R.drawable.default_bg);
        }

        holder.cardViewItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int actualPosition = holder.getAdapterPosition();
                row_index = actualPosition;
                notifyDataSetChanged();
                updateItemCateg.callBack(actualPosition, new ArrayList<>());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView name;
        CardView cardViewItems;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.hor_img);
            name = itemView.findViewById(R.id.hor_text);
            cardViewItems = itemView.findViewById(R.id.cardViewItems);
        }
    }
}
