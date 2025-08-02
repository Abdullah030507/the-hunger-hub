package com.example.bhajiDishStore.repository;

import com.example.bhajiDishStore.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
