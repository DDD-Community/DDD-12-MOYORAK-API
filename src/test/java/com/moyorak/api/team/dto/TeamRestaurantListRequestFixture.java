package com.moyorak.api.team.dto;

import com.moyorak.api.team.domain.SortOption;

public class TeamRestaurantListRequestFixture {
    public static TeamRestaurantListRequest fixture(
            SortOption sortOption, Integer size, Integer currentPage) {
        return new TeamRestaurantListRequest(sortOption, size, currentPage);
    }
}
