package com.example.bhajiDishStore.service.impl;

import com.example.bhajiDishStore.dto.CartItemRequest;
import com.example.bhajiDishStore.service.CartService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.*;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void upsertCartItems(List<CartItemRequest> items) throws Exception {
        String url = supabaseUrl + "/rest/v1/cart_items";

        HttpPost request = new HttpPost(url);
        request.setHeader("apikey", supabaseKey);
        request.setHeader("Authorization", "Bearer " + supabaseKey);
        request.setHeader("Content-Type", "application/json");
        request.setHeader("Prefer", "resolution=merge-duplicates");

        String requestBody = mapper.writeValueAsString(items);
        request.setEntity(new StringEntity(requestBody));

        try (var client = HttpClients.createDefault()) {
            var response = client.execute(request);
            String responseBody = EntityUtils.toString(response.getEntity());

            if (response.getCode() >= 400) {
                throw new RuntimeException("Supabase error: " + responseBody);
            }
        }
    }

    @Override
    public void clearCartForUser(String userId) throws Exception {
        String url = supabaseUrl + "/rest/v1/cart_items?user_id=eq." + userId;

        HttpDelete request = new HttpDelete(url);
        request.setHeader("apikey", supabaseKey);
        request.setHeader("Authorization", "Bearer " + supabaseKey);
        request.setHeader("Prefer", "return=minimal");

        try (var client = HttpClients.createDefault()) {
            var response = client.execute(request);
            if (response.getCode() >= 400) {
                String error = EntityUtils.toString(response.getEntity());
                throw new RuntimeException("Error clearing cart: " + error);
            }
        }
    }
}
