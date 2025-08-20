package com.moyorak.api.review.dto;

import java.time.LocalDateTime;

public class ReviewWithUserProjectionFixture {

    public static ReviewWithUserProjection fixture(
            Long id,
            String extraText,
            Integer score,
            Integer servingTime,
            Integer waitingTime,
            Long userId,
            String name,
            String profileImage,
            LocalDateTime createdDate) {
        return new ReviewWithUserProjection(
                id,
                extraText,
                score,
                servingTime,
                waitingTime,
                userId,
                name,
                profileImage,
                createdDate);
    }

    public static ReviewWithUserProjection defaultFixture() {
        return fixture(1L, "좋은식당", 5, 5, 5, 1L, "아무개", "/images/profile.png", LocalDateTime.now());
    }
}
