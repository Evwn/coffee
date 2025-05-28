package com.example.coffeeshop.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coffeeshop.R;
import com.example.coffeeshop.adapters.CartAdapter;
import com.example.coffeeshop.models.CartItem;
import com.example.coffeeshop.viewmodels.SharedViewModel;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment implements CartAdapter.OnCartItemChangeListener {

    private RecyclerView recyclerView;
    private CartAdapter adapter;
    private TextView totalAmountText;
    private Button checkoutButton;
    private View emptyView;
    private SharedViewModel sharedViewModel;
    private NavController navController;

    public CartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        // Initialize ViewModel
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Initialize views
        recyclerView = view.findViewById(R.id.cart_recycler_view);
        totalAmountText = view.findViewById(R.id.total_amount);
        checkoutButton = view.findViewById(R.id.checkout_button);
        emptyView = view.findViewById(R.id.empty_cart_text);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // Set up adapter with empty list initially
        adapter = new CartAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);
        
        // Set up checkout button
        checkoutButton.setOnClickListener(v -> {
            if (adapter.getItemCount() > 0) {
                // Place the order (this will also clear the cart)
                sharedViewModel.placeOrder();
                
                // Navigate to orders screen
                navController.navigate(R.id.action_cart_to_orders);
                
                // Show success message
                Toast.makeText(getContext(), "Order placed successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Your cart is empty", Toast.LENGTH_SHORT).show();
            }
        });
        
        return view;
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        
        // Observe cart items from ViewModel
        sharedViewModel.getCartItems().observe(getViewLifecycleOwner(), cartItems -> {
            adapter.updateCartItems(cartItems);
            updateEmptyView(cartItems.isEmpty());
        });
        
        // Observe cart total from ViewModel
        sharedViewModel.getCartTotal().observe(getViewLifecycleOwner(), total -> {
            totalAmountText.setText(String.format("Total: $%.2f", total));
        });
    }
    
    @Override
    public void onQuantityChanged(int position, int newQuantity) {
        sharedViewModel.updateCartItemQuantity(position, newQuantity);
    }
    
    @Override
    public void onItemRemoved(int position) {
        sharedViewModel.removeFromCart(position);
    }
    
    private void updateEmptyView(boolean isEmpty) {
        if (isEmpty) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            checkoutButton.setEnabled(false);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            checkoutButton.setEnabled(true);
        }
    }
}
