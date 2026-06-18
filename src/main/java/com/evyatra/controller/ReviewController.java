package com.evyatra.controller;

import com.evyatra.dto.ReviewRequest;
import com.evyatra.dto.ReviewResponse;
import com.evyatra.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReviewController {

    private final ReviewService reviewService;

    // Review add karo
    @PostMapping
    public ResponseEntity<?> addReview(
            @RequestBody ReviewRequest request,
            @AuthenticationPrincipal String userEmail) {

        try {
            return ResponseEntity.ok(
                    reviewService.addReview(request, userEmail));
        }
        catch (Exception e) {
            e.printStackTrace();

            return ResponseEntity.status(500)
                    .body(e.getMessage());
        }
    }

    // Station ke reviews dekho
    @GetMapping("/station/{stationId}")
    public ResponseEntity<List<ReviewResponse>> getReviews(
            @PathVariable Long stationId) {
        return ResponseEntity.ok(reviewService.getStationReviews(stationId));
    }

    @GetMapping("/check/{stationId}")
    public ResponseEntity<Map<String, Boolean>> checkReviewed(
            @PathVariable Long stationId,
            @AuthenticationPrincipal String userEmail) {
        boolean hasReviewed = reviewService.hasUserReviewed(userEmail, stationId);
        Map<String, Boolean> result = new HashMap<>();
        result.put("hasReviewed", hasReviewed);
        return ResponseEntity.ok(result);
    }
}