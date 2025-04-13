package com.Xische.billing.strategy;

import com.Xische.billing.dto.BillRequest;
import org.springframework.stereotype.Component;
import com.Xische.billing.model.Item;
import java.math.BigDecimal;

@Component
public class AffiliateDiscountStrategy implements DiscountStrategy {

    @Override
    public BigDecimal apply(BillRequest request) {
        BigDecimal eligibleAmount = request.getItems().stream()
                .filter(item -> !"GROCERY".equalsIgnoreCase(item.getCategory()))
                .map(Item::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return eligibleAmount.multiply(BigDecimal.valueOf(0.10));
    }
}
