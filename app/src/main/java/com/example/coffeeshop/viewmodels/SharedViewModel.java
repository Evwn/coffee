package com.example.coffeeshop.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.coffeeshop.models.CartItem;
import com.example.coffeeshop.models.CoffeeItem;
import com.example.coffeeshop.models.Order;
import com.example.coffeeshop.models.OrderItem;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<List<CartItem>> cartItems = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Integer> cartItemCount = new MutableLiveData<>(0);
    private final MutableLiveData<Double> cartTotal = new MutableLiveData<>(0.0);
    
    private final MutableLiveData<List<Order>> orders = new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<Order>> getOrders() {
        return orders;
    }
    
    public void placeOrder() {
        List<CartItem> currentCart = cartItems.getValue();
        if (currentCart == null || currentCart.isEmpty()) {
            return;
        }
        
        // Create order items from cart items
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : currentCart) {
            orderItems.add(new OrderItem(
                cartItem.getCoffeeItem().getName(),
                cartItem.getQuantity(),
                cartItem.getCoffeeItem().getPrice()
            ));
        }
        
        // Create new order
        String orderId = "#" + System.currentTimeMillis();
        double total = cartTotal.getValue() != null ? cartTotal.getValue() : 0.0;
        Order newOrder = new Order(orderId, new Date(), orderItems, total, "Processing");
        
        // Add to orders list
        List<Order> currentOrders = orders.getValue();
        if (currentOrders == null) {
            currentOrders = new ArrayList<>();
        }
        List<Order> updatedOrders = new ArrayList<>(currentOrders);
        updatedOrders.add(0, newOrder); // Add new order to the top
        orders.setValue(updatedOrders);
        
        // Start order status updates
        startOrderStatusUpdates(newOrder.getOrderId());
        
        // Clear cart after placing order
        clearCart();
    }
    
    private void startOrderStatusUpdates(String orderId) {
        // First update after 30 seconds to "Confirmed"
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                updateOrderStatus(orderId, "Confirmed");
                
                // Schedule the final update to "Completed" after 30 more seconds
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        updateOrderStatus(orderId, "Completed");
                    }
                }, TimeUnit.SECONDS.toMillis(30));
            }
        }, TimeUnit.SECONDS.toMillis(30));
    }
    
    private void updateOrderStatus(String orderId, String newStatus) {
        List<Order> currentOrders = orders.getValue();
        if (currentOrders == null) return;
        
        // Create a new list to trigger LiveData update
        List<Order> updatedOrders = new ArrayList<>();
        boolean updated = false;
        
        for (Order order : currentOrders) {
            // Create a new Order object if status needs to be updated
            if (order.getOrderId().equals(orderId)) {
                Order updatedOrder = new Order(
                    order.getOrderId(),
                    order.getOrderDate(),
                    order.getItems(),
                    order.getTotalAmount(),
                    newStatus
                );
                updatedOrder.setExpanded(order.isExpanded());
                updatedOrders.add(updatedOrder);
                updated = true;
                Log.d("SharedViewModel", "Updating order status: " + orderId + " to " + newStatus);
            } else {
                updatedOrders.add(order);
            }
        }
        
        if (updated) {
            // Post the new list to trigger observers
            orders.postValue(updatedOrders);
        }
    }

    public LiveData<List<CartItem>> getCartItems() {
        return cartItems;
    }

    public LiveData<Integer> getCartItemCount() {
        return cartItemCount;
    }

    public LiveData<Double> getCartTotal() {
        return cartTotal;
    }

    public void addToCart(CoffeeItem coffeeItem) {
        addToCart(coffeeItem, 1);
    }
    
    public void addToCart(CoffeeItem coffeeItem, int quantity) {
        List<CartItem> currentItems = cartItems.getValue();
        if (currentItems == null) {
            currentItems = new ArrayList<>();
        }

        boolean itemExists = false;
        for (CartItem item : currentItems) {
            if (item.getCoffeeItem().getName().equals(coffeeItem.getName())) {
                item.setQuantity(item.getQuantity() + quantity);
                itemExists = true;
                break;
            }
        }

        if (!itemExists) {
            currentItems.add(new CartItem(coffeeItem, quantity));
        }

        updateCart(currentItems);
    }
    
    public void addToCart(CartItem cartItem) {
        List<CartItem> currentItems = cartItems.getValue();
        if (currentItems == null) {
            currentItems = new ArrayList<>();
        }
        currentItems.add(new CartItem(cartItem.getCoffeeItem(), cartItem.getQuantity()));
        updateCart(currentItems);
    }

    public void updateCartItemQuantity(int position, int newQuantity) {
        List<CartItem> currentItems = cartItems.getValue();
        if (currentItems != null && position >= 0 && position < currentItems.size()) {
            if (newQuantity > 0) {
                currentItems.get(position).setQuantity(newQuantity);
            } else {
                currentItems.remove(position);
            }
            updateCart(currentItems);
        }
    }

    public void removeFromCart(int position) {
        List<CartItem> currentItems = cartItems.getValue();
        if (currentItems != null && position >= 0 && position < currentItems.size()) {
            currentItems.remove(position);
            updateCart(currentItems);
        }
    }

    public void clearCart() {
        updateCart(new ArrayList<>());
    }

    private void updateCart(List<CartItem> items) {
        cartItems.setValue(items);
        
        // Update item count
        int count = 0;
        double total = 0.0;
        
        for (CartItem item : items) {
            count += item.getQuantity();
            total += item.getTotalPrice();
        }
        
        cartItemCount.setValue(count);
        cartTotal.setValue(total);
    }
}
