package com.moyorak.api.party.dto;

import com.moyorak.api.restaurant.domain.RestaurantCategory;

public record PartyRestaurantProjection(
        Long id,
        String name,
        RestaurantCategory restaurantCategory,
        double averageReviewScore,
        Integer reviewCount) {}
