package com.moyorak.api.team.dto;

import com.moyorak.api.restaurant.domain.RestaurantCategory;

public record TeamRestaurantSummary(
        Long teamRestaurantId,
        String restaurantName,
        RestaurantCategory restaurantCategory,
        double averageReviewScore,
        int reviewCount) {}
