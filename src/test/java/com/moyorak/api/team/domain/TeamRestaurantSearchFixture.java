package com.moyorak.api.team.domain;

import org.springframework.test.util.ReflectionTestUtils;

public class TeamRestaurantSearchFixture {
    public static TeamRestaurantSearch fixture(
            Long teamRestaurantId,
            double averageReviewScore,
            double distanceFromTeam,
            double longitude,
            double latitude,
            String name,
            boolean use,
            Long teamId) {
        TeamRestaurantSearch teamRestaurantSearch = new TeamRestaurantSearch();
        ReflectionTestUtils.setField(teamRestaurantSearch, "teamRestaurantId", teamRestaurantId);
        ReflectionTestUtils.setField(
                teamRestaurantSearch, "averageReviewScore", averageReviewScore);
        ReflectionTestUtils.setField(teamRestaurantSearch, "distanceFromTeam", distanceFromTeam);
        ReflectionTestUtils.setField(teamRestaurantSearch, "longitude", longitude);
        ReflectionTestUtils.setField(teamRestaurantSearch, "latitude", latitude);
        ReflectionTestUtils.setField(teamRestaurantSearch, "name", name);
        ReflectionTestUtils.setField(teamRestaurantSearch, "use", use);
        ReflectionTestUtils.setField(teamRestaurantSearch, "teamId", teamId);
        return teamRestaurantSearch;
    }
}
