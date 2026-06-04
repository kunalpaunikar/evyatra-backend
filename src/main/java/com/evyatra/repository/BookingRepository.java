package com.evyatra.repository;

import com.evyatra.model.Booking;
import com.evyatra.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // User All bookings
    List<Booking> findByUser(User user);

    // active bookings of User
    List<Booking> findByUserAndStatus(User user, Booking.BookingStatus status);
}