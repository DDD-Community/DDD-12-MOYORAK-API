package com.moyorak.api.auth.dto;

import java.time.LocalDateTime;

public class ReviewWithUserAndTeamRestaurantProjectionFixture {

    public static ReviewWithUserAndTeamRestaurantProjection fixture(
            Long id,
            Long teamRestaurantId,
            boolean isDeletedTeamRestaurant,
            String restaurantName,
            String extraText,
            Integer score,
            Integer servingTime,
            Integer waitingTime,
            Long userId,
            String name,
            String profileImage,
            LocalDateTime createdDate) {
        return new ReviewWithUserAndTeamRestaurantProjection(
                id,
                restaurantName,
                teamRestaurantId,
                isDeletedTeamRestaurant,
                extraText,
                score,
                servingTime,
                waitingTime,
                userId,
                name,
                profileImage,
                createdDate);
    }

    public static ReviewWithUserAndTeamRestaurantProjection defaultFixture() {
        return fixture(
                1L,
                1L,
                true,
                "식당",
                "좋은식당",
                5,
                5,
                5,
                1L,
                "아무개",
                "/images/profile.png",
                LocalDateTime.now());
    }
}
