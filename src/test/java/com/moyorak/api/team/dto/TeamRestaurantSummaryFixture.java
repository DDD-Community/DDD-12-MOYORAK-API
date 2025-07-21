package com.moyorak.api.team.dto;

import com.moyorak.api.restaurant.domain.RestaurantCategory;

public class TeamRestaurantSummaryFixture {

    public static TeamRestaurantSummary fixture(
            Long teamRestaurantId,
            String restaurantName,
            RestaurantCategory restaurantCategory,
            double averageReviewScore,
            int reviewCount) {
        return new TeamRestaurantSummary(
                teamRestaurantId,
                restaurantName,
                restaurantCategory,
                averageReviewScore,
                reviewCount);
    }
}
