package com.moyorak.api.team.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

@Getter
@RequiredArgsConstructor
public enum SortOption {
    DISTANCE("distanceFromTeam", Sort.Direction.ASC),
    RATING("averageReviewScore", Sort.Direction.DESC),
    RECENT("createdDate", Sort.Direction.DESC),
    NAME("restaurantName", Sort.Direction.DESC);

    private final String field;
    private final Sort.Direction direction;

    public Sort toSort() {
        return Sort.by(direction, field);
    }

    @JsonCreator
    public static SortOption of(final String input) {
        if (!StringUtils.hasText(input)) {
            return null;
        }

        for (SortOption sortOption : SortOption.values()) {
            if (sortOption.name().equals(input)) {
                return sortOption;
            }
        }
        return null;
    }
}
