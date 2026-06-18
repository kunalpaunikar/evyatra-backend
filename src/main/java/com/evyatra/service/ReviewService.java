package com.evyatra.service;

import com.evyatra.dto.ReviewRequest;
import com.evyatra.dto.ReviewResponse;
import com.evyatra.exception.BadRequestException;
import com.evyatra.exception.ResourceNotFoundException;
import com.evyatra.model.*;
import com.evyatra.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final BookingRepository bookingRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final EvStationRepository stationRepository;

    // Review submit karo
    public ReviewResponse addReview(ReviewRequest request, String userEmail) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User nahi mila!"));

        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking nahi mili!"));

        EvStation station = booking.getStation();

        if (request.getRating() < 1 || request.getRating() > 5) {
            throw new BadRequestException("Rating 1 se 5 ke beech honi chahiye!");
        }

        if (reviewRepository.existsByUserIdAndStationId(
                user.getId(),
                station.getId())) {

            throw new BadRequestException(
                    "Aap pehle se review de chuke hain!");
        }

        Review review = new Review();
        review.setUser(user);
        review.setStation(station);
        review.setBookingId(booking.getId());
        review.setRating(request.getRating());
        review.setComment(request.getComment());

        reviewRepository.save(review);

        Double avg =
                reviewRepository.getAverageRatingByStationId(station.getId());

        Long total =
                reviewRepository.countByStationId(station.getId());

        return new ReviewResponse(
                review.getId(),
                user.getName(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt(),
                avg,
                total
        );
    }

    // Station ke saare reviews
    public List<ReviewResponse> getStationReviews(Long stationId) {

        EvStation station = stationRepository.findById(stationId)
                .orElseThrow(() -> new ResourceNotFoundException("Station nahi mila!"));

        Double avg = reviewRepository.getAverageRatingByStationId(stationId);
        Long total = (long) reviewRepository.findByStationOrderByCreatedAtDesc(station).size();

        return reviewRepository.findByStationOrderByCreatedAtDesc(station)
                .stream()
                .map(r -> new ReviewResponse(
                        r.getId(),
                        r.getUser().getName(),
                        r.getRating(),
                        r.getComment(),
                        r.getCreatedAt(),
                        avg,
                        total
                ))
                .collect(Collectors.toList());
    }

    public boolean hasUserReviewed(String userEmail, Long stationId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User nahi mila!"));
        return reviewRepository.existsByUserIdAndStationId(user.getId(), stationId);
    }
}