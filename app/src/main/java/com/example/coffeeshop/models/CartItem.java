package com.example.coffeeshop.models;

public class CartItem {
    private CoffeeItem coffeeItem;
    private int quantity;

    public CartItem(CoffeeItem coffeeItem, int quantity) {
        this.coffeeItem = coffeeItem;
        this.quantity = quantity;
    }

    public CoffeeItem getCoffeeItem() {
        return coffeeItem;
    }

    public void setCoffeeItem(CoffeeItem coffeeItem) {
        this.coffeeItem = coffeeItem;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public double getTotalPrice() {
        return coffeeItem.getPrice() * quantity;
    }
}
