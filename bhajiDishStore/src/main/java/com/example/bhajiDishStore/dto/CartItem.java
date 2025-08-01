package com.example.bhajiDishStore.dto;

import java.util.UUID;

public class CartItem {
    private UUID id;
    private int quantity;
    private String source;
    
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}
    
    
}
