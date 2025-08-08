package com.moyorak.api.party.domain;

import com.moyorak.api.party.dto.PartyRestaurantProjection;
import com.moyorak.api.restaurant.domain.RestaurantCategory;

public class PartyRestaurantProjectionFixture {
    public static PartyRestaurantProjection fixture(
            Long id,
            String name,
            RestaurantCategory restaurantCategory,
            double averageReviewScore,
            Integer reviewCount) {
        return new PartyRestaurantProjection(
                id, name, restaurantCategory, averageReviewScore, reviewCount);
    }
}
