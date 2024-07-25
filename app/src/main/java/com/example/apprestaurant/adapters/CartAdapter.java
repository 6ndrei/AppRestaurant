package com.example.apprestaurant.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.apprestaurant.R;
import com.example.apprestaurant.models.CartModel;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private List<CartModel> list;
    private Context mContext;

    public CartAdapter(Context context, List<CartModel> list) {
        this.mContext = context;
        this.list = list;
    }

    public void setList(List<CartModel> newList) {
        this.list = newList;
    }

    @NonNull
    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.cart_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.ViewHolder holder, int position) {
        CartModel model = list.get(position);
        Glide.with(mContext)
                .load(model.getImage())
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(holder.imageView);
        holder.name.setText(model.getName());
        holder.price.setText(model.getPrice() + " RON");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView imageView;
        TextView name, price, currency;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.detailed_img);
            name = itemView.findViewById(R.id.CartItemName);
            price = itemView.findViewById(R.id.cartItemPrice);
            currency = itemView.findViewById(R.id.RONcart);
        }
    }
}
