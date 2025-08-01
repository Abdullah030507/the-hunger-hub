package com.example.bhajiDishStore.dto;

import java.math.BigDecimal;

public class CheckoutResponse {
    private BigDecimal total;

    public CheckoutResponse(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
