package com.Xische.billing.strategy;

import com.Xische.billing.dto.BillRequest;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import com.Xische.billing.model.Item;

@Component
public class LoyalCustomerDiscountStrategy implements DiscountStrategy {

    @Override
    public BigDecimal apply(BillRequest request) {
        long years = ChronoUnit.YEARS.between(request.getCustomerSince(), LocalDate.now());

        if (years < 2) {
            return BigDecimal.ZERO;
        }

        BigDecimal discountableTotal = request.getItems().stream()
                .filter(item -> !"GROCERY".equalsIgnoreCase(item.getCategory()))
                .map(Item::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return discountableTotal.multiply(BigDecimal.valueOf(0.05));
    }
}