package com.example.coffeeshop.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coffeeshop.R;
import com.example.coffeeshop.models.Order;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class OrdersAdapter extends ListAdapter<Order, OrdersAdapter.OrderViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(Order order);
    }
    
    private OnItemClickListener listener;
    private static final DiffUtil.ItemCallback<Order> DIFF_CALLBACK = new DiffUtil.ItemCallback<Order>() {
        @Override
        public boolean areItemsTheSame(@NonNull Order oldItem, @NonNull Order newItem) {
            return oldItem.getOrderId().equals(newItem.getOrderId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Order oldItem, @NonNull Order newItem) {
            return oldItem.equals(newItem);
        }
    };

    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());

    public OrdersAdapter() {
        super(DIFF_CALLBACK);
    }
    
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = getItem(position);
        
        holder.orderIdText.setText(order.getOrderId());
        holder.orderDateText.setText(dateFormat.format(order.getOrderDate()));
        holder.itemCountText.setText(String.format("%d items", order.getItemCount()));
        holder.totalAmountText.setText(String.format("$%.2f", order.getTotalAmount()));
        holder.statusText.setText(order.getStatus());
        
        // Set status color
        int statusColor;
        switch (order.getStatus().toLowerCase()) {
            case "completed":
                statusColor = holder.itemView.getContext().getColor(R.color.coffeeshop_green_500);
                break;
            case "confirmed":
                statusColor = holder.itemView.getContext().getColor(R.color.coffeeshop_blue_500);
                break;
            case "processing":
                statusColor = holder.itemView.getContext().getColor(R.color.coffeeshop_orange_500);
                break;
            case "cancelled":
                statusColor = holder.itemView.getContext().getColor(R.color.coffeeshop_red_500);
                break;
            default:
                statusColor = holder.itemView.getContext().getColor(R.color.coffeeshop_gray_500);
        }
        holder.statusText.setTextColor(statusColor);
        
        // Toggle order details visibility
        boolean isExpanded = order.isExpanded();
        holder.orderDetailsLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.expandIcon.setRotation(isExpanded ? 180 : 0);
        
        // Set up order items
        if (isExpanded) {
            // Clear previous views
            holder.orderItemsLayout.removeAllViews();
            
            // Add order items
            for (int i = 0; i < order.getItems().size(); i++) {
                View itemView = LayoutInflater.from(holder.itemView.getContext())
                        .inflate(R.layout.item_order_product, holder.orderItemsLayout, false);
                
                TextView itemName = itemView.findViewById(R.id.item_name);
                TextView itemQuantity = itemView.findViewById(R.id.item_quantity);
                TextView itemPrice = itemView.findViewById(R.id.item_price);
                
                itemName.setText(order.getItems().get(i).getName());
                itemQuantity.setText(String.format("x%d", order.getItems().get(i).getQuantity()));
                itemPrice.setText(String.format("$%.2f", order.getItems().get(i).getTotalPrice()));
                
                holder.orderItemsLayout.addView(itemView);
                
                // Add divider if not the last item
                if (i < order.getItems().size() - 1) {
                    View divider = new View(holder.itemView.getContext());
                    divider.setLayoutParams(new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            (int) (1 * holder.itemView.getContext().getResources().getDisplayMetrics().density)
                    ));
                    divider.setBackgroundColor(holder.itemView.getContext().getColor(R.color.coffeeshop_gray_200));
                    holder.orderItemsLayout.addView(divider);
                }
            }
        }
        
        // Toggle expand/collapse on the entire item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(order);
            }
            
            // Toggle expand/collapse
            order.setExpanded(!order.isExpanded());
            notifyItemChanged(position);
        });
    }

    // getItemCount() is provided by ListAdapter

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderIdText;
        TextView orderDateText;
        TextView itemCountText;
        TextView totalAmountText;
        TextView statusText;
        View orderDetailsLayout;
        ViewGroup orderItemsLayout;
        ImageView expandIcon;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdText = itemView.findViewById(R.id.order_id);
            orderDateText = itemView.findViewById(R.id.order_date);
            itemCountText = itemView.findViewById(R.id.item_count);
            totalAmountText = itemView.findViewById(R.id.total_amount);
            statusText = itemView.findViewById(R.id.status_text);
            orderDetailsLayout = itemView.findViewById(R.id.order_details_layout);
            orderItemsLayout = itemView.findViewById(R.id.order_items_layout);
            expandIcon = itemView.findViewById(R.id.expand_icon);
        }
    }
}
