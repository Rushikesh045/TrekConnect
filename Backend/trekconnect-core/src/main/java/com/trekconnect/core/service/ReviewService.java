package com.trekconnect.core.service;

import com.trekconnect.core.entity.Review;
import com.trekconnect.core.repository.ReviewRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service managing trekker reviews and ratings.
 * 
 * WHY THIS SERVICE WAS CREATED:
 * Handles submitting post-trek reviews and fetching ratings for specific treks.
 */
@Service
public class ReviewService {

    private static final Logger logger = LoggerFactory.getLogger(ReviewService.class);

    private final ReviewRepository reviewRepository;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    /**
     * Submits a 1-5 star review for a trek.
     */
    @Transactional
    public Review submitReview(String userId, String userName, String trekId, Integer rating, String comment) {
        logger.info("Submitting review by User: {}, Trek: {}, Rating: {}", userId, trekId, rating);

        if (rating == null || rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5 stars");
        }

        Review review = Review.builder()
                .userId(userId)
                .userName(userName != null ? userName : "Trekker")
                .trekId(trekId)
                .rating(rating)
                .comment(comment)
                .build();

        return reviewRepository.save(review);
    }

    /**
     * Retrieves all reviews for a specific trek.
     */
    @Transactional(readOnly = true)
    public List<Review> getTrekReviews(String trekId) {
        logger.debug("Fetching reviews for Trek: {}", trekId);
        return reviewRepository.findByTrekIdOrderByCreatedAtDesc(trekId);
    }
}
