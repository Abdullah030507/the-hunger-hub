package com.example.bhajiDishStore.controller;

import com.example.bhajiDishStore.dto.CartItem;
import com.example.bhajiDishStore.dto.CheckoutResponse;
import com.example.bhajiDishStore.service.BillingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/checkout")
@CrossOrigin(origins = "*")
public class BillingController {

    @Autowired
    private BillingService billingService;

    
    @PostMapping
    public ResponseEntity<?> checkout(@RequestBody List<CartItem> cartItems) {
        try {
            CheckoutResponse response = billingService.calculateBill(cartItems);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Checkout failed: " + e.getMessage());
        }
    }

}
