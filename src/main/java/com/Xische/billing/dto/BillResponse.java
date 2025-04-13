package com.Xische.billing.dto;

import java.math.BigDecimal;

public class BillResponse {
    private BigDecimal originalAmount;
    private BigDecimal payableAmount;
    private String currency;

    public BillResponse(BigDecimal originalAmount, BigDecimal payableAmount, String currency) {
        this.originalAmount = originalAmount;
        this.payableAmount = payableAmount;
        this.currency = currency;
    }

    public BigDecimal getOriginalAmount() {
        return originalAmount;
    }

    public void setOriginalAmount(BigDecimal originalAmount) {
        this.originalAmount = originalAmount;
    }

    public BigDecimal getPayableAmount() {
        return payableAmount;
    }

    public void setPayableAmount(BigDecimal payableAmount) {
        this.payableAmount = payableAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
