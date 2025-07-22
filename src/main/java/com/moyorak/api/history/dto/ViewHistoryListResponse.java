package com.moyorak.api.history.dto;

import com.moyorak.api.restaurant.domain.RestaurantCategory;
import com.moyorak.api.review.domain.FirstReviewPhotoPaths;
import com.moyorak.api.team.domain.TeamRestaurantSummaries;
import com.moyorak.api.team.dto.TeamRestaurantSummary;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Schema(title = "팀 맛집 상세 조회 기록 리스트 응답 DTO")
public record ViewHistoryListResponse(
        @ArraySchema(
                        schema =
                                @Schema(
                                        description = "조회 기록",
                                        implementation = ViewHistoryResponse.class),
                        arraySchema = @Schema(description = "조회 기록 응답 리스트"))
                List<ViewHistoryResponse> viewHistories) {
    public static ViewHistoryListResponse from(
            final ViewHistorySummaries viewHistorySummaries,
            final TeamRestaurantSummaries teamRestaurantSummaries,
            final FirstReviewPhotoPaths firstReviewPhotoPaths) {
        final Map<Long, TeamRestaurantSummary> teamRestaurantSummaryMap =
                teamRestaurantSummaries.getSummaries().stream()
                        .collect(
                                Collectors.toUnmodifiableMap(
                                        TeamRestaurantSummary::teamRestaurantId,
                                        Function.identity()));

        final List<ViewHistoryResponse> viewHistoryListResponse =
                viewHistorySummaries.summaries().stream()
                        .map(
                                viewHistorySummary -> {
                                    final TeamRestaurantSummary teamRestaurantSummary =
                                            teamRestaurantSummaryMap.get(
                                                    viewHistorySummary.teamRestaurantId());
                                    // 팀 식당이 존재하지 않는 경우
                                    if (teamRestaurantSummary == null) {
                                        return ViewHistoryResponse.create(
                                                viewHistorySummary.id(),
                                                0L,
                                                "",
                                                RestaurantCategory.ETC,
                                                0.0,
                                                0,
                                                null);
                                    }

                                    return ViewHistoryResponse.create(
                                            viewHistorySummary.id(),
                                            teamRestaurantSummary.teamRestaurantId(),
                                            teamRestaurantSummary.restaurantName(),
                                            teamRestaurantSummary.restaurantCategory(),
                                            teamRestaurantSummary.averageReviewScore(),
                                            teamRestaurantSummary.reviewCount(),
                                            firstReviewPhotoPaths.getPhotoPath(
                                                    teamRestaurantSummary.teamRestaurantId()));
                                })
                        .toList();

        return new ViewHistoryListResponse(viewHistoryListResponse);
    }
}
