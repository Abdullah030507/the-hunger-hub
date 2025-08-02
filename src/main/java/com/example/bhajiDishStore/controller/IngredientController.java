package com.example.bhajiDishStore.controller;

import com.example.bhajiDishStore.model.Ingredient;
import com.example.bhajiDishStore.service.IngredientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ingredients")
@CrossOrigin("*")
public class IngredientController {

    @Autowired
    private IngredientService ingredientService;

    @GetMapping
    public List<Ingredient> getIngredients() {
        return ingredientService.getAllIngredients();
    }
}
