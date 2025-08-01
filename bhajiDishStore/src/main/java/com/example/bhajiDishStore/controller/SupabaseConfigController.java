package com.example.bhajiDishStore.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class SupabaseConfigController {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    @GetMapping("/supabase-config")
    public Map<String, String> getSupabaseConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("url", supabaseUrl);
        config.put("key", supabaseKey);
        return config;
    }
}
