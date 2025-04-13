package com.Xische.billing.strategy;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class FlatDiscountStrategy {
    public BigDecimal apply(BigDecimal totalAmount) {
        int hundreds = totalAmount.divide(BigDecimal.valueOf(100), RoundingMode.FLOOR).intValue();
        return BigDecimal.valueOf(hundreds * 5L);
    }
}
