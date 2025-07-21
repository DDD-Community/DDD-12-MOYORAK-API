package com.moyorak.api.team.dto;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public record ListResult(List<Long> ids, Pageable pageable, Long total) {
    public Page<TeamRestaurantListResponse> toPage(
            final List<TeamRestaurantListResponse> teamRestaurantListResponses) {

        final Map<Long, TeamRestaurantListResponse> responseMap =
                teamRestaurantListResponses.stream()
                        .collect(
                                Collectors.toUnmodifiableMap(
                                        TeamRestaurantListResponse::teamRestaurantId,
                                        Function.identity()));

        final List<TeamRestaurantListResponse> responsesInSearchOrder =
                ids.stream().map(responseMap::get).filter(Objects::nonNull).toList();

        return new PageImpl<>(responsesInSearchOrder, pageable, total);
    }
}
