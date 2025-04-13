package com.Xische.billing.client;

import com.Xische.billing.dto.ExchangeRateResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Component
public class CurrencyExchangeClient {

    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${EXCHANGE-API-KEY}")
    String apiKey;

    @Value("${EXCHANGE-URI}")
    String exchangeUri;
    @Cacheable("exchangeRates")
    public Map<String, BigDecimal> getRates(String baseCurrency) {
        String url = exchangeUri + apiKey + "/latest/" + baseCurrency;
        ExchangeRateResponse response = restTemplate.getForObject(url, ExchangeRateResponse.class);
        if (!"success".equalsIgnoreCase(response.getResult())) {
            throw new IllegalArgumentException("Failed to fetch exchange rates. Error: " +
                    response.getErrorType());
        }
        return response.getConversionRates();
    }

    public BigDecimal convert(BigDecimal amount, String fromCurrency, String toCurrency) {
        Map<String, BigDecimal> rates = getRates(fromCurrency);
        BigDecimal rate = rates.get(toCurrency);
        if (rate == null) {
            throw new IllegalArgumentException("No exchange rate found for target currency: " + toCurrency);
        }
        return amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
    }
}

