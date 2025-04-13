package com.Xische.billing.service;

import com.Xische.billing.client.CurrencyExchangeClient;
import com.Xische.billing.dto.BillRequest;
import com.Xische.billing.dto.BillResponse;
import com.Xische.billing.strategy.*;
import org.springframework.stereotype.Service;
import com.Xische.billing.model.Item;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class BillingService {

    private final List<DiscountStrategy> strategies;
    private final FlatDiscountStrategy flatStrategy;
    private final CurrencyExchangeClient currencyClient;

    public BillingService(List<DiscountStrategy> strategies, FlatDiscountStrategy flatStrategy, CurrencyExchangeClient currencyClient) {
        this.strategies = strategies;
        this.flatStrategy = flatStrategy;
        this.currencyClient = currencyClient;
    }

    public BillResponse calculate(BillRequest request) {
        BigDecimal total = request.getItems().stream()
                .map(Item::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal percentageDiscount = getApplicablePercentageDiscount(request);
        BigDecimal flatDiscount = flatStrategy.apply(total);

        BigDecimal discountedAmount = total.subtract(percentageDiscount).subtract(flatDiscount);
        BigDecimal converted = currencyClient.convert(discountedAmount, request.getOriginalCurrency(), request.getTargetCurrency());

        return new BillResponse(total,converted,request.getTargetCurrency());
    }

    private BigDecimal getApplicablePercentageDiscount(BillRequest request) {
        if (request.getUserType().equalsIgnoreCase("EMPLOYEE")) {
            return strategies.stream()
                    .filter(s -> s instanceof EmployeeDiscountStrategy)
                    .findFirst()
                    .map(s -> s.apply(request))
                    .orElse(BigDecimal.ZERO);
        } else if (request.getUserType().equalsIgnoreCase("AFFILIATE")) {
            return strategies.stream()
                    .filter(s -> s instanceof AffiliateDiscountStrategy)
                    .findFirst()
                    .map(s -> s.apply(request))
                    .orElse(BigDecimal.ZERO);
        } else if (ChronoUnit.YEARS.between(request.getCustomerSince(), LocalDate.now()) > 2) {
            return strategies.stream()
                    .filter(s -> s instanceof LoyalCustomerDiscountStrategy)
                    .findFirst()
                    .map(s -> s.apply(request))
                    .orElse(BigDecimal.ZERO);
        }
        return BigDecimal.ZERO;
    }

}
