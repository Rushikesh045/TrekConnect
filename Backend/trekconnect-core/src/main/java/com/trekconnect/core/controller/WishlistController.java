package com.trekconnect.core.controller;

import com.trekconnect.core.entity.Wishlist;
import com.trekconnect.core.service.WishlistService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for Phase 7 Wishlist Management.
 */
@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    private static final Logger logger = LoggerFactory.getLogger(WishlistController.class);

    private final WishlistService wishlistService;

    @Autowired
    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @PostMapping("/toggle")
    public ResponseEntity<Map<String, Object>> toggleWishlist(@AuthenticationPrincipal String userId,
                                                              @RequestBody Map<String, String> body) {
        String effectiveUser = userId != null ? userId : "usr-1";
        String trekId = body.get("trekId");

        logger.info("REST Request: POST /api/wishlist/toggle for Trek: {}", trekId);
        boolean isSaved = wishlistService.toggleWishlist(effectiveUser, trekId);
        return ResponseEntity.ok(Map.of("trekId", trekId, "isSaved", isSaved));
    }

    @GetMapping("/my-wishlist")
    public ResponseEntity<List<Wishlist>> getMyWishlist(@AuthenticationPrincipal String userId) {
        String effectiveUser = userId != null ? userId : "usr-1";
        logger.info("REST Request: GET /api/wishlist/my-wishlist for User: {}", effectiveUser);
        return ResponseEntity.ok(wishlistService.getMyWishlist(effectiveUser));
    }
}
