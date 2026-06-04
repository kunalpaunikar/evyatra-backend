package com.evyatra.controller;

import com.evyatra.dto.AdminBookingResponse;
import com.evyatra.dto.BookingRequest;
import com.evyatra.dto.BookingResponse;
import com.evyatra.model.Booking;
import com.evyatra.repository.BookingRepository;
import com.evyatra.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class BookingController {

    private final BookingService bookingService;
    private final BookingRepository bookingRepository;

    // Check this in BookingController
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @RequestBody BookingRequest request,
            @AuthenticationPrincipal String userEmail) {

        // Isn't userEmail coming as null?
        System.out.println("User email: " + userEmail);

        return ResponseEntity.ok(bookingService.createBooking(request, userEmail));
    }

    // Show me all my bookings
    @GetMapping("/my")
    public ResponseEntity<List<BookingResponse>> getMyBookings(
            @AuthenticationPrincipal String userEmail) {
        return ResponseEntity.ok(bookingService.getMyBookings(userEmail));
    }

    // Cancel the Booking
    @PutMapping("/{id}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(
            @PathVariable Long id,
            @AuthenticationPrincipal String userEmail) {
        return ResponseEntity.ok(bookingService.cancelBooking(id, userEmail));
    }

    // Admin — Show All Bookings.
    @GetMapping("/api/admin/bookings")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<AdminBookingResponse>> getAllBookings() {
        List<Booking> bookings = bookingRepository.findAll();
        List<AdminBookingResponse> response = bookings.stream()
                .map(b -> new AdminBookingResponse(
                        b.getId(),
                        b.getUser().getName(),
                        b.getUser().getEmail(),
                        b.getStation().getName(),
                        b.getStation().getCity(),
                        b.getSlot().getSlotNumber(),
                        b.getBookingDate().toString(),
                        b.getStartTime().toString(),
                        b.getEndTime().toString(),
                        b.getTotalAmount(),
                        b.getStatus().name(),
                        b.getCreatedAt().toString()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}
