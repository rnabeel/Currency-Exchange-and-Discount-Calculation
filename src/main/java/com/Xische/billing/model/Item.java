package com.Xische.billing.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Item {
    private String name;
    private String category;
    private BigDecimal price;

    public Item(String name, String category, BigDecimal price) {
        this.name = name;
        this.category = category;
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public BigDecimal getPrice() {
        return price;
    }
}
