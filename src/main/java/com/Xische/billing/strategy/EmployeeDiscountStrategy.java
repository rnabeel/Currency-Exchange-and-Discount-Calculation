package com.Xische.billing.strategy;

import com.Xische.billing.dto.BillRequest;
import org.springframework.stereotype.Component;
import com.Xische.billing.model.Item;
import java.math.BigDecimal;

@Component
public class EmployeeDiscountStrategy implements DiscountStrategy {
    @Override
    public BigDecimal apply(BillRequest request) {
        BigDecimal discountableTotal = request.getItems().stream()
                .filter(item -> !item.getCategory().equalsIgnoreCase("GROCERY"))
                .map(Item::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return discountableTotal.multiply(BigDecimal.valueOf(0.30));
    }
}
