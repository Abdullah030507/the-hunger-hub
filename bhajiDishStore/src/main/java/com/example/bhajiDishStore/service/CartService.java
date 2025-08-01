package com.example.bhajiDishStore.service;

import com.example.bhajiDishStore.dto.CartItemRequest;

import java.util.List;

public interface CartService {
    void upsertCartItems(List<CartItemRequest> items) throws Exception;
    void clearCartForUser(String userId) throws Exception; 
}
