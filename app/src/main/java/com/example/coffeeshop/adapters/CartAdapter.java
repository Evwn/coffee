package com.example.coffeeshop.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.button.MaterialButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.example.coffeeshop.R;
import com.example.coffeeshop.models.CartItem;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartItems = new ArrayList<>();
    private final OnCartItemChangeListener listener;

    public interface OnCartItemChangeListener {
        void onQuantityChanged(int position, int newQuantity);
        void onItemRemoved(int position);
    }

    public CartAdapter(List<CartItem> cartItems, OnCartItemChangeListener listener) {
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        if (position < 0 || position >= cartItems.size()) {
            return;
        }
        
        CartItem item = cartItems.get(position);
        
        holder.coffeeName.setText(item.getCoffeeItem().getName());
        holder.coffeePrice.setText(String.format("$%.2f", item.getCoffeeItem().getPrice()));
        holder.quantityText.setText(String.valueOf(item.getQuantity()));
        holder.totalPrice.setText(String.format("$%.2f", item.getTotalPrice()));
        
        // In a real app, you would load the image using Glide or Picasso
        holder.coffeeImage.setImageResource(item.getCoffeeItem().getImageResId());
        
        // Set up click listeners
        holder.increaseButton.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                int newQuantity = item.getQuantity() + 1;
                if (listener != null) {
                    listener.onQuantityChanged(adapterPosition, newQuantity);
                }
            }
        });
        
        holder.decreaseButton.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                int newQuantity = item.getQuantity() - 1;
                if (newQuantity > 0) {
                    if (listener != null) {
                        listener.onQuantityChanged(adapterPosition, newQuantity);
                    }
                } else {
                    // Remove item if quantity becomes 0
                    if (listener != null) {
                        listener.onItemRemoved(adapterPosition);
                    }
                }
            }
        });
        
        holder.removeButton.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION && listener != null) {
                listener.onItemRemoved(adapterPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems != null ? cartItems.size() : 0;
    }
    
    public void updateCartItems(List<CartItem> newItems) {
        final List<CartItem> finalNewItems = new ArrayList<>(newItems != null ? newItems : new ArrayList<>());
        
        // Calculate the difference between old and new items
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return cartItems.size();
            }

            @Override
            public int getNewListSize() {
                return finalNewItems.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                CartItem oldItem = cartItems.get(oldItemPosition);
                CartItem newItem = finalNewItems.get(newItemPosition);
                return oldItem.getCoffeeItem().getName().equals(newItem.getCoffeeItem().getName());
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                CartItem oldItem = cartItems.get(oldItemPosition);
                CartItem newItem = finalNewItems.get(newItemPosition);
                return oldItem.getQuantity() == newItem.getQuantity() && 
                       oldItem.getTotalPrice() == newItem.getTotalPrice();
            }
        });
        
        cartItems.clear();
        cartItems.addAll(finalNewItems);
        diffResult.dispatchUpdatesTo(this);
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView coffeeImage;
        TextView coffeeName;
        TextView coffeePrice;
        TextView quantityText;
        TextView totalPrice;
        MaterialButton increaseButton;
        MaterialButton decreaseButton;
        MaterialButton removeButton;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            coffeeImage = itemView.findViewById(R.id.coffee_image);
            coffeeName = itemView.findViewById(R.id.coffee_name);
            coffeePrice = itemView.findViewById(R.id.coffee_price);
            quantityText = itemView.findViewById(R.id.quantity_text);
            totalPrice = itemView.findViewById(R.id.total_price);
            increaseButton = itemView.findViewById(R.id.increase_button);
            decreaseButton = itemView.findViewById(R.id.decrease_button);
            removeButton = itemView.findViewById(R.id.remove_button);
        }
    }
}
