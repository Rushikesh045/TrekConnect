package com.trekconnect.core.controller;

import com.trekconnect.core.entity.Review;
import com.trekconnect.core.service.ReviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for Phase 7 Reviews & Ratings.
 */
@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private static final Logger logger = LoggerFactory.getLogger(ReviewController.class);

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<Review> submitReview(@AuthenticationPrincipal String userId,
                                                @RequestBody Map<String, Object> body) {
        String effectiveUser = userId != null ? userId : "usr-1";
        String trekId = (String) body.get("trekId");
        Integer rating = body.get("rating") instanceof Number ? ((Number) body.get("rating")).intValue() : 5;
        String comment = (String) body.get("comment");
        String userName = (String) body.getOrDefault("userName", "Trekker");

        logger.info("REST Request: POST /api/reviews for Trek: {}", trekId);
        Review review = reviewService.submitReview(effectiveUser, userName, trekId, rating, comment);
        return ResponseEntity.ok(review);
    }

    @GetMapping("/trek/{trekId}")
    public ResponseEntity<List<Review>> getReviews(@PathVariable String trekId) {
        logger.info("REST Request: GET /api/reviews/trek/{}", trekId);
        return ResponseEntity.ok(reviewService.getTrekReviews(trekId));
    }
}
