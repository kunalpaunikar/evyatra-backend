package com.evyatra.controller;

import com.evyatra.dto.PaymentRequest;
import com.evyatra.dto.PaymentResponse;
import com.evyatra.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/pay")
    public ResponseEntity<PaymentResponse> processPayment(
            @RequestBody PaymentRequest request,
            @AuthenticationPrincipal String userEmail) {
        return ResponseEntity.ok(
                paymentService.processPayment(request, userEmail)
        );
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<PaymentResponse> getPayment(
            @PathVariable Long bookingId) {
        return ResponseEntity.ok(
                paymentService.getPaymentByBooking(bookingId)
        );
    }
}