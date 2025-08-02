package com.example.bhajiDishStore.service.impl;

import com.example.bhajiDishStore.model.Ingredient;
import com.example.bhajiDishStore.service.IngredientService;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class IngredientServiceImpl implements IngredientService {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public List<Ingredient> getAllIngredients() {
        List<Ingredient> ingredients = new ArrayList<>();

        try {
            String url = supabaseUrl + "/rest/v1/ingredients?select=*";

            HttpClient client = HttpClients.createDefault();
            HttpGet request = new HttpGet(url);
            request.addHeader("apikey", supabaseKey);
            request.addHeader("Authorization", "Bearer " + supabaseKey);
            request.addHeader("Accept", "application/json");

            ClassicHttpResponse response = (ClassicHttpResponse) client.executeOpen(null, request, null);
            String json = EntityUtils.toString(response.getEntity());

            JsonNode root = mapper.readTree(json);
            for (JsonNode node : root) {
                Ingredient ingredient = new Ingredient();
                ingredient.setId(node.get("id").asText());
                ingredient.setName(node.get("name").asText());
                ingredient.setPrice(node.get("price").asDouble());
                ingredient.setQuantity(node.get("quantity").asInt());
                ingredient.setImageURL(node.get("image_url").asText());
                ingredients.add(ingredient);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ingredients;
    }
}
