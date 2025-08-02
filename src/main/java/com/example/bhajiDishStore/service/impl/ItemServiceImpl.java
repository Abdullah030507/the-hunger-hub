package com.example.bhajiDishStore.service.impl;

import com.example.bhajiDishStore.model.Item;
import com.example.bhajiDishStore.repository.ItemRepository;
import com.example.bhajiDishStore.service.ItemService;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.ClassicHttpRequests;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public List<Item> getAllItems() {
        List<Item> items = new ArrayList<>();

        try {
            String url = supabaseUrl + "/rest/v1/items?select=*";

            HttpClient client = HttpClients.createDefault();
            HttpGet request = new HttpGet(url);
            request.addHeader("apikey", supabaseKey);
            request.addHeader("Authorization", "Bearer " + supabaseKey);
            request.addHeader("Accept", "application/json");

            ClassicHttpResponse response = (ClassicHttpResponse) client.executeOpen(null, request, null);

            String json = EntityUtils.toString(response.getEntity());
            JsonNode root = mapper.readTree(json);

            for (JsonNode node : root) {
                Item item = new Item();
                item.setId(node.get("id").asText());
                item.setName(node.get("name").asText());
                item.setQuantity(node.get("quantity").asInt());
                item.setPrice(node.get("price").asDouble());
                item.setImageURL(node.get("image_url").asText());
                items.add(item);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return items;
    }
}
