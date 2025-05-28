package com.example.coffeeshop.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coffeeshop.R;
import com.example.coffeeshop.models.CoffeeItem;

import java.util.List;

public class CoffeeAdapter extends RecyclerView.Adapter<CoffeeAdapter.CoffeeViewHolder> {

    private List<CoffeeItem> coffeeItems;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(CoffeeItem item);
        void onAddToCartClick(CoffeeItem item);
    }

    public CoffeeAdapter(List<CoffeeItem> coffeeItems, OnItemClickListener listener) {
        this.coffeeItems = coffeeItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CoffeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_coffee, parent, false);
        return new CoffeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CoffeeViewHolder holder, int position) {
        CoffeeItem item = coffeeItems.get(position);
        holder.coffeeName.setText(item.getName());
        holder.coffeePrice.setText(String.format("$%.2f", item.getPrice()));
        
        // In a real app, you would load the image using Glide or Picasso
        holder.coffeeImage.setImageResource(item.getImageResId());
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });
        
        holder.addToCartButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAddToCartClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return coffeeItems.size();
    }

    static class CoffeeViewHolder extends RecyclerView.ViewHolder {
        ImageView coffeeImage;
        TextView coffeeName;
        TextView coffeePrice;
        View addToCartButton;

        public CoffeeViewHolder(@NonNull View itemView) {
            super(itemView);
            coffeeImage = itemView.findViewById(R.id.coffee_image);
            coffeeName = itemView.findViewById(R.id.coffee_name);
            coffeePrice = itemView.findViewById(R.id.coffee_price);
            addToCartButton = itemView.findViewById(R.id.add_to_cart_button);
        }
    }
}
