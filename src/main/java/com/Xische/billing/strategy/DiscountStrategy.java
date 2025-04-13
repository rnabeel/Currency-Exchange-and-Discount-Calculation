package com.Xische.billing.strategy;

import com.Xische.billing.dto.BillRequest;

import java.math.BigDecimal;

public interface DiscountStrategy {
    BigDecimal apply(BillRequest request);
}
