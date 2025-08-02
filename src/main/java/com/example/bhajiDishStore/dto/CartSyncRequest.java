package com.example.bhajiDishStore.dto;

import java.util.List;
import java.util.UUID;

public class CartSyncRequest {
    private UUID user_id;
    private List<CartItemRequest> cart_items;

    public UUID getUser_id() {
        return user_id;
    }

    public void setUser_id(UUID user_id) {
        this.user_id = user_id;
    }

    public List<CartItemRequest> getCart_items() {
        return cart_items;
    }

    public void setCart_items(List<CartItemRequest> cart_items) {
        this.cart_items = cart_items;
    }
}
