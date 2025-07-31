package com.moyorak.api.team.domain;

import com.moyorak.api.restaurant.domain.Restaurant;
import org.springframework.test.util.ReflectionTestUtils;

public class TeamRestaurantFixture {
    public static TeamRestaurant fixture(
            Long id,
            String summary,
            double averageReviewScore,
            double averageServingTime,
            double averageWaitingTime,
            double distanceFromTeam,
            Integer reviewCount,
            boolean use,
            Long teamId,
            Restaurant restaurant) {
        TeamRestaurant teamRestaurant = new TeamRestaurant();
        ReflectionTestUtils.setField(teamRestaurant, "id", id);
        ReflectionTestUtils.setField(teamRestaurant, "summary", summary);
        ReflectionTestUtils.setField(teamRestaurant, "averageReviewScore", averageReviewScore);
        ReflectionTestUtils.setField(teamRestaurant, "averageServingTime", averageServingTime);
        ReflectionTestUtils.setField(teamRestaurant, "averageWaitingTime", averageWaitingTime);
        ReflectionTestUtils.setField(teamRestaurant, "distanceFromTeam", distanceFromTeam);
        ReflectionTestUtils.setField(teamRestaurant, "reviewCount", reviewCount);
        ReflectionTestUtils.setField(teamRestaurant, "use", use);
        ReflectionTestUtils.setField(teamRestaurant, "teamId", teamId);
        ReflectionTestUtils.setField(teamRestaurant, "restaurant", restaurant);
        return teamRestaurant;
    }

    public static TeamRestaurant fixture(
            Long id,
            Double averageReviewScore,
            Integer reviewCount,
            boolean use,
            Long teamId,
            Restaurant restaurant) {
        TeamRestaurant teamRestaurant = new TeamRestaurant();
        ReflectionTestUtils.setField(teamRestaurant, "id", id);
        ReflectionTestUtils.setField(teamRestaurant, "averageReviewScore", averageReviewScore);
        ReflectionTestUtils.setField(teamRestaurant, "reviewCount", reviewCount);
        ReflectionTestUtils.setField(teamRestaurant, "use", use);
        ReflectionTestUtils.setField(teamRestaurant, "teamId", teamId);
        ReflectionTestUtils.setField(teamRestaurant, "restaurant", restaurant);
        return teamRestaurant;
    }
}
