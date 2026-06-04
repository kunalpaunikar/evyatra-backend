package com.evyatra.service;

import com.evyatra.dto.PaymentRequest;
import com.evyatra.dto.PaymentResponse;
import com.evyatra.exception.BadRequestException;
import com.evyatra.exception.ResourceNotFoundException;
import com.evyatra.model.*;
import com.evyatra.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    public PaymentResponse processPayment(
            PaymentRequest request, String userEmail) {

        // Search Booking
        Booking booking = bookingRepository
                .findById(request.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Booking nahi mili!"
                ));

        // Already paid?
        paymentRepository.findByBookingId(request.getBookingId())
                .ifPresent(p -> {
                    if (p.getPaymentStatus() == Payment.PaymentStatus.SUCCESS) {
                        throw new BadRequestException(
                                "Ye booking already paid hai!"
                        );
                    }
                });

        // Search User
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User nahi mila!"
                ));

        // Generate Transaction ID
        String transactionId = "EVY" + UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();

        // Create Payment object
        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setUser(user);
        payment.setAmount(booking.getTotalAmount());
        payment.setPaymentMethod(
                Payment.PaymentMethod.valueOf(request.getPaymentMethod())
        );
        payment.setPaymentStatus(Payment.PaymentStatus.SUCCESS);
        payment.setTransactionId(transactionId);
        payment.setPaidAt(LocalDateTime.now());

        // CONFIRMED Booking status
        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        Payment saved = paymentRepository.save(payment);

        return new PaymentResponse(
                saved.getId(),
                booking.getId(),
                saved.getAmount(),
                saved.getPaymentMethod().name(),
                saved.getPaymentStatus().name(),
                saved.getTransactionId(),
                saved.getPaidAt(),
                booking.getStation().getName(),
                "✅ Payment successful! Transaction ID: " + transactionId
        );
    }

    public PaymentResponse getPaymentByBooking(Long bookingId) {
        Payment payment = paymentRepository
                .findByBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payment nahi mili!"
                ));

        return new PaymentResponse(
                payment.getId(),
                bookingId,
                payment.getAmount(),
                payment.getPaymentMethod().name(),
                payment.getPaymentStatus().name(),
                payment.getTransactionId(),
                payment.getPaidAt(),
                payment.getBooking().getStation().getName(),
                "Payment found"
        );
    }
}