package com.evyatra.dto;

import lombok.Data;

@Data
public class PaymentRequest {
    private Long bookingId;
    private String paymentMethod; // UPI, CARD, NETBANKING, WALLET
    private String upiId;         // UPI
    private String cardNumber;    // Card (last 4 digits)
}