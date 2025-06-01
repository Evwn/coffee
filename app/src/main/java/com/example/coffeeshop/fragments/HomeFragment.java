package com.example.coffeeshop.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textfield.TextInputEditText;

import com.example.coffeeshop.R;
import com.example.coffeeshop.adapters.CoffeeAdapter;
import com.example.coffeeshop.models.CoffeeItem;
import com.example.coffeeshop.viewmodels.SharedViewModel;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    
    private RecyclerView recyclerView;
    private CoffeeAdapter adapter;
    private List<CoffeeItem> coffeeItems;
    private SharedViewModel sharedViewModel;
    private TextInputLayout searchBar;
    private NavController navController;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        
        // Initialize ViewModel
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        
        // Initialize SearchBar
        searchBar = view.findViewById(R.id.search_bar);
        TextInputEditText searchEditText = (TextInputEditText) searchBar.getEditText();
        if (searchEditText != null) {
            searchEditText.setOnClickListener(v -> {
                // Handle search bar click
                Toast.makeText(requireContext(), "Search clicked", Toast.LENGTH_SHORT).show();
            });
        }
        
        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.coffee_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        
        // Initialize FAB
        FloatingActionButton fabCart = view.findViewById(R.id.fab_cart);
        fabCart.setOnClickListener(v -> {
            // Navigate to cart
            if (navController != null) {
                navController.navigate(R.id.navigation_cart);
            }
        });
        
        // Initialize coffee items
        initializeCoffeeItems();
        
        // Set up adapter
        adapter = new CoffeeAdapter(coffeeItems, new CoffeeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(CoffeeItem item) {
                // Handle item click (e.g., show details)
                Toast.makeText(getContext(), "Selected: " + item.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAddToCartClick(CoffeeItem item) {
                // Add item to cart via ViewModel
                sharedViewModel.addToCart(item);
                Toast.makeText(getContext(), "Added to cart: " + item.getName(), Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setAdapter(adapter);
        
        return view;
    }
    
    private void initializeCoffeeItems() {
        coffeeItems = new ArrayList<>();
        coffeeItems.add(new CoffeeItem("Espresso", "Strong black coffee", 2.50, R.drawable.coffee_espresso));
        coffeeItems.add(new CoffeeItem("Cappuccino", "Espresso with steamed milk", 3.50, R.drawable.coffee_cappuccino));
        coffeeItems.add(new CoffeeItem("Latte", "Espresso with lots of milk", 4.00, R.drawable.coffee_latte));
        coffeeItems.add(new CoffeeItem("Mocha", "Chocolate flavored coffee", 4.50, R.drawable.coffee_mocha));
        coffeeItems.add(new CoffeeItem("Americano", "Espresso with hot water", 3.00, R.drawable.coffee_americano));
        coffeeItems.add(new CoffeeItem("Macchiato", "Espresso with a dash of milk", 3.25, R.drawable.coffee_macchiato));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initialize NavController using the fragment's view
        navController = Navigation.findNavController(view);
    }
    

}
