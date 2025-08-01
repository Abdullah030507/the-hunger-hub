package com.example.bhajiDishStore.controller;

import com.example.bhajiDishStore.dto.CartItemRequest;
import com.example.bhajiDishStore.dto.CartSyncRequest;
import com.example.bhajiDishStore.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping
    public ResponseEntity<String> syncCart(@RequestBody CartSyncRequest request) {
        try {
            if (request.getUser_id() == null) {
                return ResponseEntity.badRequest().body("Missing user ID.");
            }

            String userId = request.getUser_id().toString();
            List<CartItemRequest> items = request.getCart_items();

            cartService.clearCartForUser(userId); // Always clear old cart

            if (items != null && !items.isEmpty()) {
                cartService.upsertCartItems(items); // Only insert if not empty
            }

            return ResponseEntity.ok("Cart synced.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Cart sync failed: " + e.getMessage());
        }
    }


}
