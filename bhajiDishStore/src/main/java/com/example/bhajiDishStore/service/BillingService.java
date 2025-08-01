package com.example.bhajiDishStore.service;

import com.example.bhajiDishStore.dto.CartItem;
import com.example.bhajiDishStore.dto.CheckoutResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpPatch;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BillingService {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public CheckoutResponse calculateBill(List<CartItem> items) {
        BigDecimal total = BigDecimal.ZERO;

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            for (CartItem cartItem : items) {

                // Only "items" and "ingredients" are expected
                // String table = cartItem.getSource().equalsIgnoreCase("ingredients") ? "ingredients" : "items";
            	String source = cartItem.getSource() != null ? cartItem.getSource() : "items";
            	String table = source.equalsIgnoreCase("ingredients") ? "ingredients" : "items";

                // GET the item/ingredient from Supabase
                String getUrl = supabaseUrl + "/rest/v1/" + table + "?id=eq." + cartItem.getId();
                HttpGet getRequest = new HttpGet(getUrl);
                getRequest.addHeader("apikey", supabaseKey);
                getRequest.addHeader("Authorization", "Bearer " + supabaseKey);
                getRequest.addHeader("Accept", "application/json");

                ClassicHttpResponse getResponse = (ClassicHttpResponse) client.execute(getRequest);
                String json = EntityUtils.toString(getResponse.getEntity());
                JsonNode node = objectMapper.readTree(json).get(0); // only 1 item expected

                int currentQty = node.get("quantity").asInt();
                String name = node.get("name").asText();
                double price = node.get("price").asDouble();

                if (currentQty < cartItem.getQuantity()) {
                    throw new RuntimeException("Not enough stock for " + name);
                }

                int updatedQty = currentQty - cartItem.getQuantity();

                // PATCH to update quantity
                String patchUrl = supabaseUrl + "/rest/v1/" + table + "?id=eq." + cartItem.getId();
                HttpPatch patchRequest = new HttpPatch(patchUrl);
                patchRequest.addHeader("apikey", supabaseKey);
                patchRequest.addHeader("Authorization", "Bearer " + supabaseKey);
                patchRequest.addHeader("Content-Type", "application/json");
                patchRequest.addHeader("Prefer", "return=minimal");

                String payload = "{\"quantity\": " + updatedQty + "}";
                patchRequest.setEntity(new StringEntity(payload));
                client.execute(patchRequest).close();

                BigDecimal itemTotal = BigDecimal.valueOf(price)
                        .multiply(BigDecimal.valueOf(cartItem.getQuantity()));
                total = total.add(itemTotal);
            }

            // Save the order to Supabase
            String orderUrl = supabaseUrl + "/rest/v1/orders";
            HttpPost postRequest = new HttpPost(orderUrl);
            postRequest.addHeader("apikey", supabaseKey);
            postRequest.addHeader("Authorization", "Bearer " + supabaseKey);
            postRequest.addHeader("Content-Type", "application/json");
            postRequest.addHeader("Prefer", "return=minimal");

            String orderJson = objectMapper.writeValueAsString(items);
            String orderPayload = String.format("{\"items\": %s, \"total\": %s}", orderJson, total);
            postRequest.setEntity(new StringEntity(orderPayload));
            client.execute(postRequest).close();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Checkout failed: " + e.getMessage());
        }

        return new CheckoutResponse(total);
    }
}
