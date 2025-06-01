package com.example.coffeeshop;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.coffeeshop.viewmodels.SharedViewModel;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private NavController navController;
    private AppBarConfiguration appBarConfiguration;
    private BadgeDrawable cartBadge;
    private SharedViewModel sharedViewModel;
    private BottomNavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.util.Log.d("MainActivity", "onCreate: Starting...");
        setContentView(R.layout.coffeeshop_activity_main);

        // Initialize ViewModel
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

        // Set up the Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar == null) {
            android.util.Log.e("MainActivity", "Toolbar not found in layout!");
        } else {
            android.util.Log.d("MainActivity", "Toolbar found, setting up...");
            setSupportActionBar(toolbar);
        }

        // Set up navigation
        try {
            appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.navigation_home,
                    R.id.navigation_cart,
                    R.id.navigation_orders)
                    .build();
            android.util.Log.d("MainActivity", "AppBarConfiguration created successfully");
        } catch (Exception e) {
            android.util.Log.e("MainActivity", "Error creating AppBarConfiguration", e);
        }

        // Set up the NavController
        try {
            navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
            android.util.Log.d("MainActivity", "NavController initialized successfully");
            
            // Set up the BottomNavigationView
            navView = findViewById(R.id.nav_view);
            if (navView == null) {
                android.util.Log.e("MainActivity", "BottomNavigationView not found!");
            } else {
                try {
                    NavigationUI.setupWithNavController(navView, navController);
                    android.util.Log.d("MainActivity", "BottomNavigationView setup complete");
                } catch (Exception e) {
                    android.util.Log.e("MainActivity", "Error setting up BottomNavigationView", e);
                }
            }
        } catch (Exception e) {
            android.util.Log.e("MainActivity", "Error initializing NavController", e);
        }
        
        // Handle toolbar title and up button
        if (navController == null) {
            android.util.Log.e("MainActivity", "NavController is null, cannot set up destination changed listener");
            return;
        }
        
        try {
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                android.util.Log.d("MainActivity", "Navigated to: " + destination.getLabel() + " (ID: " + destination.getId() + ")");
            // Update toolbar title based on the current destination
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(destination.getLabel());
            }
            
            // Show/hide bottom navigation based on destination
            if (destination.getId() == R.id.navigation_home || 
                destination.getId() == R.id.navigation_cart || 
                destination.getId() == R.id.navigation_orders) {
                navView.setVisibility(View.VISIBLE);
            } else {
                navView.setVisibility(View.GONE);
            }
            });
            
            // Set up the ActionBar with NavController
            if (getSupportActionBar() != null) {
                try {
                    NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
                    android.util.Log.d("MainActivity", "ActionBar setup with NavController complete");
                } catch (Exception e) {
                    android.util.Log.e("MainActivity", "Error setting up ActionBar with NavController", e);
                }
            } else {
                android.util.Log.e("MainActivity", "ActionBar is null, cannot set up with NavController");
            }
        } catch (Exception e) {
            android.util.Log.e("MainActivity", "Error setting up navigation", e);
        }
        
        // Set up cart badge
        if (navView != null) {
            Menu menu = navView.getMenu();
            cartBadge = navView.getOrCreateBadge(menu.findItem(R.id.navigation_cart).getItemId());
            cartBadge.setBackgroundColor(getResources().getColor(R.color.coffeeshop_red_500));
            cartBadge.setBadgeTextColor(getResources().getColor(android.R.color.white));
            cartBadge.setMaxCharacterCount(2);
            cartBadge.setVisible(false);
        } else {
            android.util.Log.e("MainActivity", "navView is null, cannot set up cart badge");
        }
        
        // Observe cart item count
        sharedViewModel.getCartItemCount().observe(this, count -> {
            if (count > 0) {
                cartBadge.setNumber(count);
                cartBadge.setVisible(true);
            } else {
                cartBadge.setVisible(false);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}