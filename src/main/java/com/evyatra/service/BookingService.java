package com.evyatra.service;

import com.evyatra.dto.BookingRequest;
import com.evyatra.dto.BookingResponse;
import com.evyatra.exception.BadRequestException;
import com.evyatra.exception.ResourceNotFoundException;
import com.evyatra.model.*;
import com.evyatra.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ChargerSlotRepository slotRepository;
    private final EvStationRepository stationRepository;
    private final UserRepository userRepository;

    // Do the booking
    public BookingResponse createBooking(BookingRequest request, String userEmail) {

        // Find the User
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User nahi mila!"));

        // Find the Station
        EvStation station = stationRepository.findById(request.getStationId())
                .orElseThrow(() -> new ResourceNotFoundException("Station nahi mila!"));

        // Find the Booking slot.
        ChargerSlot slot = slotRepository.findById(request.getSlotId())
                .orElseThrow(() -> new ResourceNotFoundException("Slot nahi mila!"));

        // is Slot available ?
        if (slot.getStatus() != ChargerSlot.SlotStatus.AVAILABLE) {
            throw new BadRequestException("Slot is already booked!");
        }

        // Calculate the total Amount.
        long hours = Duration.between(
                request.getStartTime(),
                request.getEndTime()
        ).toHours();
        Double totalAmount = hours * station.getPricePerUnit();

        // make a Booking
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setStation(station);
        booking.setSlot(slot);
        booking.setBookingDate(request.getBookingDate());
        booking.setStartTime(request.getStartTime());
        booking.setEndTime(request.getEndTime());
        booking.setTotalAmount(totalAmount);
        booking.setStatus(Booking.BookingStatus.CONFIRMED);

        // Book the Slot Status
        slot.setStatus(ChargerSlot.SlotStatus.BOOKED);
        slotRepository.save(slot);

        // Save the Booking
        Booking saved = bookingRepository.save(booking);

        return mapToResponse(saved);
    }

    // All bookings of User
    public List<BookingResponse> getMyBookings(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not Found !"));  // ✅

        return bookingRepository.findByUser(user)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Cancel the Booking
    public BookingResponse cancelBooking(Long bookingId, String userEmail) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not Found !"));  // ✅

        // You can only cancel your booking.
        if (!booking.getUser().getEmail().equals(userEmail)) {
            throw new BadRequestException("This is not your booking!");  // ✅
        }

        // cancelled the status
        booking.setStatus(Booking.BookingStatus.CANCELLED);

        // Make the slot available again
        ChargerSlot slot = booking.getSlot();
        slot.setStatus(ChargerSlot.SlotStatus.AVAILABLE);
        slotRepository.save(slot);

        return mapToResponse(bookingRepository.save(booking));
    }

    // Booking → convert response
    private BookingResponse mapToResponse(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getStation().getId(),  // ← stationId add karo
                booking.getStation().getName(),
                booking.getSlot().getSlotNumber(),
                booking.getSlot().getChargerType().name(),
                booking.getBookingDate(),
                booking.getStartTime(),
                booking.getEndTime(),
                booking.getTotalAmount(),
                booking.getStatus().name()
        );
    }
}