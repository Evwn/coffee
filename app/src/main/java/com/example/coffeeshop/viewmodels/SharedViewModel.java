package com.example.coffeeshop.viewmodels;

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
        // First update after 2 minutes to "Confirmed"
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                updateOrderStatus(orderId, "Confirmed");
                
                // Schedule the final update to "Completed" after 2 more minutes
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        updateOrderStatus(orderId, "Completed");
                    }
                }, TimeUnit.MINUTES.toMillis(2));
            }
        }, TimeUnit.MINUTES.toMillis(2));
    }
    
    private void updateOrderStatus(String orderId, String newStatus) {
        List<Order> currentOrders = orders.getValue();
        if (currentOrders == null) return;
        
        List<Order> updatedOrders = new ArrayList<>();
        boolean updated = false;
        
        for (Order order : currentOrders) {
            if (order.getOrderId().equals(orderId)) {
                order.setStatus(newStatus);
                updated = true;
            }
            updatedOrders.add(order);
        }
        
        if (updated) {
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
        List<CartItem> currentItems = cartItems.getValue();
        if (currentItems == null) {
            currentItems = new ArrayList<>();
        }

        boolean itemExists = false;
        for (CartItem item : currentItems) {
            if (item.getCoffeeItem().getName().equals(coffeeItem.getName())) {
                item.setQuantity(item.getQuantity() + 1);
                itemExists = true;
                break;
            }
        }

        if (!itemExists) {
            currentItems.add(new CartItem(coffeeItem, 1));
        }

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
