package com.example.coffeeshop.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coffeeshop.R;
import com.example.coffeeshop.adapters.OrdersAdapter;
import com.example.coffeeshop.models.CartItem;
import com.example.coffeeshop.models.CoffeeItem;
import com.example.coffeeshop.models.Order;
import com.example.coffeeshop.models.OrderItem;
import com.example.coffeeshop.viewmodels.SharedViewModel;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import java.util.List;

public class OrdersFragment extends Fragment {

    private static final String TAG = "OrdersFragment";
    
    private RecyclerView recyclerView;
    private OrdersAdapter adapter;
    private TextView emptyView;
    private ExtendedFloatingActionButton fabNewOrder;
    private SharedViewModel sharedViewModel;
    private NavController navController;

    public OrdersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);
        Log.d(TAG, "onCreateView: Initializing views");

        // Initialize NavController
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
        
        // Initialize ViewModel
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        
        // Initialize views
        recyclerView = view.findViewById(R.id.orders_recycler_view);
        emptyView = view.findViewById(R.id.empty_view);
        fabNewOrder = view.findViewById(R.id.fab_new_order);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // Initialize adapter
        adapter = new OrdersAdapter();
        
        // Set click listener for order items
        adapter.setOnItemClickListener(order -> {
            // Handle order item click - show order details or reorder
            showReorderConfirmation(order);
        });
        recyclerView.setAdapter(adapter);
        
        // Set up click listeners
        fabNewOrder.setOnClickListener(v -> startNewOrder());
        
        return view;
    }
    
    private void startNewOrder() {
        Log.d(TAG, "startNewOrder: Starting new order");
        // Clear any existing cart items
        sharedViewModel.clearCart();
        // Navigate to home to start new order
        navController.navigate(R.id.navigation_home);
    }
    
    private void showReorderConfirmation(Order order) {
        Log.d(TAG, "showReorderConfirmation: Reordering order: " + order.getOrderId());
        // Show a confirmation dialog or snackbar
        if (getView() != null) {
            Snackbar.make(getView(), "Add all items from this order to cart?", Snackbar.LENGTH_LONG)
                .setAction("REORDER", v -> reorderItems(order))
                .setActionTextColor(getResources().getColor(R.color.coffeeshop_coffee_primary))
                .show();
        }
    }
    
    private void reorderItems(Order order) {
        try {
            Log.d(TAG, "reorderItems: Adding items to cart from order: " + order.getOrderId());
            // Clear current cart
            sharedViewModel.clearCart();
            
            // Add all items from the order to the cart
            for (OrderItem orderItem : order.getItems()) {
                // Create a new CoffeeItem from the OrderItem
                CoffeeItem coffeeItem = new CoffeeItem(
                    orderItem.getName(),  // name
                    "",                    // description
                    orderItem.getPrice(),  // price
                    0                      // imageResId - using 0 as default
                );
                
                // Add to cart with quantity
                sharedViewModel.addToCart(coffeeItem, orderItem.getQuantity());
            }
            
            // Show success message
            if (getView() != null) {
                Snackbar.make(getView(), "Items added to cart", Snackbar.LENGTH_SHORT).show();
                
                // Add a small delay before navigation to ensure cart is updated
                getView().postDelayed(() -> {
                    // Navigate to cart
                    navController.navigate(R.id.navigation_cart);
                }, 300);
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error reordering items: " + e.getMessage(), e);
            if (getView() != null) {
                Snackbar.make(getView(), "Error reordering items: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        }
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: Setting up observers");
        
        // Observe orders from ViewModel
        sharedViewModel.getOrders().observe(getViewLifecycleOwner(), orders -> {
            if (orders != null) {
                Log.d(TAG, "Orders updated, count: " + orders.size());
                adapter.submitList(orders);
                updateEmptyView(orders.isEmpty());
            } else {
                Log.w(TAG, "Orders list is null");
                updateEmptyView(true);
            }
        });
    }
    
    private void updateEmptyView(boolean isEmpty) {
        if (isEmpty) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }
}
