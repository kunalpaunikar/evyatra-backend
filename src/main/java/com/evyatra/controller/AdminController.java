package com.evyatra.controller;

import com.evyatra.dto.AdminBookingResponse;
import com.evyatra.dto.UserProfileResponse;
import com.evyatra.exception.BadRequestException;
import com.evyatra.exception.ResourceNotFoundException;
import com.evyatra.model.Booking;
import com.evyatra.model.EvStation;
import com.evyatra.model.Payment;
import com.evyatra.model.User;
import com.evyatra.repository.BookingRepository;
import com.evyatra.repository.EvStationRepository;
import com.evyatra.repository.PaymentRepository;
import com.evyatra.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;
import com.evyatra.repository.ReviewRepository;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController {

    private final UserRepository userRepository;
    private final EvStationRepository stationRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final ReviewRepository reviewRepository;

    // All users
    @GetMapping("/users")
    public ResponseEntity<List<UserProfileResponse>> getAllUsers() {
        List<UserProfileResponse> users = userRepository.findAll()
                .stream()
                .map(u -> new UserProfileResponse(
                        u.getId(),
                        u.getName(),
                        u.getEmail(),
                        u.getPhone(),
                        u.getRole().name()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    // All bookings — for admin
    @GetMapping("/bookings")
    public ResponseEntity<List<AdminBookingResponse>> getAllBookings() {
        List<AdminBookingResponse> response = bookingRepository.findAll()
                .stream()
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

    // delete the User
    @DeleteMapping("/users/{id}")
    @Transactional
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));

        if (user.getRole() == User.Role.ROLE_ADMIN) {
            throw new BadRequestException("cannot delete the Admin!");
        }

        // Delete the Booking of User First
        List<Booking> userBookings = bookingRepository.findByUser(user);
        for (Booking booking : userBookings) {
            paymentRepository.findByBookingId(booking.getId())
                    .ifPresent(payment -> paymentRepository.delete(payment));
        }

        // After that delete the booking
        bookingRepository.deleteAll(userBookings);

        // After Delete the User
        userRepository.deleteById(id);
        return ResponseEntity.ok("✅ User is delete!");
    }

    // Add New Station
    @PostMapping("/stations")
    public ResponseEntity<EvStation> addStation(
            @RequestBody EvStation station) {
        station.setStatus(EvStation.Status.ACTIVE);
        return ResponseEntity.ok(stationRepository.save(station));
    }

    // delete the Station
    @DeleteMapping("/stations/{id}")
    public ResponseEntity<String> deleteStation(@PathVariable Long id) {
        stationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Station nahi mila!"
                ));
        stationRepository.deleteById(id);
        return ResponseEntity.ok("✅ delete the Station!");
    }

    // Station status toggle
    @PutMapping("/stations/{id}/toggle")
    public ResponseEntity<EvStation> toggleStation(@PathVariable Long id) {
        EvStation station = stationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Station not Found!"
                ));

        station.setStatus(
                station.getStatus() == EvStation.Status.ACTIVE
                        ? EvStation.Status.INACTIVE
                        : EvStation.Status.ACTIVE
        );

        return ResponseEntity.ok(stationRepository.save(station));
    }

    @GetMapping("/reviews")
    public ResponseEntity<?> getAllReviews() {
        List<Map<String, Object>> reviews = reviewRepository.findAll()
                .stream()
                .map(r -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("userName", r.getUser().getName());
                    map.put("stationName", r.getStation().getName());
                    map.put("rating", r.getRating());
                    map.put("comment", r.getComment());
                    map.put("createdAt", r.getCreatedAt());
                    return map;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(reviews);
    }

    // Dashboard stats
    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        long totalUsers = userRepository.count();
        long totalStations = stationRepository.count();
        long totalBookings = bookingRepository.count();

        return ResponseEntity.ok(new java.util.HashMap<>() {{
            put("totalUsers", totalUsers);
            put("totalStations", totalStations);
            put("totalBookings", totalBookings);
        }});
    }
}
