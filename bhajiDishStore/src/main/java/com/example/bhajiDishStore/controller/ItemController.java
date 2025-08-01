package com.example.bhajiDishStore.controller;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.bhajiDishStore.model.Item;
import com.example.bhajiDishStore.service.ItemService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@CrossOrigin("*")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @GetMapping
    public List<Item> getItems() {
        return itemService.getAllItems();
    }
}
