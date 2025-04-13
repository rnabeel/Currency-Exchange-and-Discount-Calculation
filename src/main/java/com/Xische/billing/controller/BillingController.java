package com.Xische.billing.controller;

import com.Xische.billing.dto.BillRequest;
import com.Xische.billing.dto.BillResponse;
import com.Xische.billing.service.BillingService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class BillingController {

    private final BillingService billingService;

    public BillingController(BillingService billingService) {
        this.billingService = billingService;
    }

    @PostMapping("/calculate")
    public ResponseEntity<BillResponse> calculate(@RequestBody @Validated BillRequest request) {
        return ResponseEntity.ok(billingService.calculate(request));
    }
}
