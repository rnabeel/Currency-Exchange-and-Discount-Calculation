package com.Xische.billing;

import com.Xische.billing.client.CurrencyExchangeClient;
import com.Xische.billing.dto.BillRequest;
import com.Xische.billing.dto.BillResponse;
import com.Xische.billing.model.Item;
import com.Xische.billing.service.BillingService;
import com.Xische.billing.strategy.AffiliateDiscountStrategy;
import com.Xische.billing.strategy.EmployeeDiscountStrategy;
import com.Xische.billing.strategy.FlatDiscountStrategy;
import com.Xische.billing.strategy.LoyalCustomerDiscountStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class BillingServiceTest {

    private BillingService billingService;
    private CurrencyExchangeClient currencyClient;
    private FlatDiscountStrategy flatDiscountStrategy;
    private EmployeeDiscountStrategy employeeDiscount;
    private AffiliateDiscountStrategy affiliateDiscount;
    private LoyalCustomerDiscountStrategy loyalCustomerDiscount;

    @BeforeEach
    void setUp() {
        currencyClient = mock(CurrencyExchangeClient.class);
        flatDiscountStrategy = mock(FlatDiscountStrategy.class);
        employeeDiscount = mock(EmployeeDiscountStrategy.class);
        affiliateDiscount = mock(AffiliateDiscountStrategy.class);
        loyalCustomerDiscount = mock(LoyalCustomerDiscountStrategy.class);

        billingService = new BillingService(
                List.of(employeeDiscount, affiliateDiscount, loyalCustomerDiscount),
                flatDiscountStrategy,
                currencyClient
        );
    }

    @Test
    void testEmployeeGets30PercentDiscountAndFlat() {
        List<Item> items = List.of(
                new Item("TV", "OTHER", BigDecimal.valueOf(1000)),
                new Item("Bread", "GROCERY", BigDecimal.valueOf(100))
        );

        BillRequest request = new BillRequest();
        request.setItems(items);
        request.setUserType("EMPLOYEE");
        request.setCustomerSince(LocalDate.of(2020, 1, 1));
        request.setOriginalCurrency("USD");
        request.setTargetCurrency("EUR");

        BigDecimal total = BigDecimal.valueOf(1100);
        BigDecimal percentDiscount = BigDecimal.valueOf(300);
        BigDecimal flatDiscount = BigDecimal.valueOf(50);
        BigDecimal discountedAmount = total.subtract(percentDiscount).subtract(flatDiscount);
        BigDecimal convertedAmount = discountedAmount.multiply(BigDecimal.valueOf(0.85));

        when(employeeDiscount.apply(any())).thenReturn(percentDiscount);
        when(flatDiscountStrategy.apply(total)).thenReturn(flatDiscount);
        when(currencyClient.convert(discountedAmount, "USD", "EUR")).thenReturn(convertedAmount);

        BillResponse response = billingService.calculate(request);

        assertEquals(total, response.getOriginalAmount());
        assertEquals(convertedAmount, response.getPayableAmount());
        assertEquals("EUR", response.getCurrency());

        verify(currencyClient).convert(discountedAmount, "USD", "EUR");
    }

    @Test
    void testAffiliateGets10PercentDiscountAndFlat() {
        List<Item> items = List.of(
                new Item("Monitor", "OTHER", BigDecimal.valueOf(400))
        );

        BillRequest request = new BillRequest();
        request.setItems(items);
        request.setUserType("AFFILIATE");
        request.setCustomerSince(LocalDate.of(2022, 1, 1));
        request.setOriginalCurrency("USD");
        request.setTargetCurrency("USD");

        BigDecimal total = BigDecimal.valueOf(400);
        BigDecimal percentDiscount = BigDecimal.valueOf(40);
        BigDecimal flatDiscount = BigDecimal.valueOf(20);
        BigDecimal discounted = total.subtract(percentDiscount).subtract(flatDiscount);

        when(affiliateDiscount.apply(any())).thenReturn(percentDiscount);
        when(flatDiscountStrategy.apply(total)).thenReturn(flatDiscount);
        when(currencyClient.convert(discounted, "USD", "USD")).thenReturn(discounted);

        BillResponse response = billingService.calculate(request);

        assertEquals(total, response.getOriginalAmount());
        assertEquals(discounted, response.getPayableAmount());
    }

    @Test
    void testLoyalCustomerGets5PercentDiscountAndFlat() {
        List<Item> items = List.of(
                new Item("Microwave", "OTHER", BigDecimal.valueOf(300))
        );

        BillRequest request = new BillRequest();
        request.setItems(items);
        request.setUserType("CUSTOMER");
        request.setCustomerSince(LocalDate.of(2019, 1, 1));
        request.setOriginalCurrency("USD");
        request.setTargetCurrency("USD");

        BigDecimal total = BigDecimal.valueOf(300);
        BigDecimal percentDiscount = BigDecimal.valueOf(15);
        BigDecimal flatDiscount = BigDecimal.valueOf(15);
        BigDecimal discounted = total.subtract(percentDiscount).subtract(flatDiscount);

        when(loyalCustomerDiscount.apply(any())).thenReturn(percentDiscount);
        when(flatDiscountStrategy.apply(total)).thenReturn(flatDiscount);
        when(currencyClient.convert(discounted, "USD", "USD")).thenReturn(discounted);

        BillResponse response = billingService.calculate(request);

        assertEquals(total, response.getOriginalAmount());
        assertEquals(discounted, response.getPayableAmount());
    }

    @Test
    void testGroceriesOnly_NoPercentageDiscount_OnlyFlat() {
        List<Item> items = List.of(
                new Item("Flour", "GROCERY", BigDecimal.valueOf(200))
        );

        BillRequest request = new BillRequest();
        request.setItems(items);
        request.setUserType("CUSTOMER");
        request.setCustomerSince(LocalDate.now());
        request.setOriginalCurrency("USD");
        request.setTargetCurrency("USD");

        BigDecimal total = BigDecimal.valueOf(200);
        BigDecimal flatDiscount = BigDecimal.valueOf(10);
        BigDecimal discounted = total.subtract(flatDiscount);

        when(flatDiscountStrategy.apply(total)).thenReturn(flatDiscount);
        when(currencyClient.convert(discounted, "USD", "USD")).thenReturn(discounted);

        BillResponse response = billingService.calculate(request);

        assertEquals(total, response.getOriginalAmount());
        assertEquals(discounted, response.getPayableAmount());
    }

    @Test
    void testNoDiscount_Below100() {
        List<Item> items = List.of(
                new Item("Chips", "GROCERY", BigDecimal.valueOf(99))
        );

        BillRequest request = new BillRequest();
        request.setItems(items);
        request.setUserType("CUSTOMER");
        request.setCustomerSince(LocalDate.now());
        request.setOriginalCurrency("USD");
        request.setTargetCurrency("USD");

        when(flatDiscountStrategy.apply(BigDecimal.valueOf(99))).thenReturn(BigDecimal.ZERO);
        when(currencyClient.convert(BigDecimal.valueOf(99), "USD", "USD")).thenReturn(BigDecimal.valueOf(99));

        BillResponse response = billingService.calculate(request);

        assertEquals(BigDecimal.valueOf(99), response.getOriginalAmount());
        assertEquals(BigDecimal.valueOf(99), response.getPayableAmount());
    }
}
