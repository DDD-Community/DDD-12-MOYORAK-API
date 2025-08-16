package com.moyorak.api.party.dto;

import com.moyorak.api.restaurant.domain.RestaurantCategory;
import com.moyorak.api.review.domain.FirstReviewPhotoPaths;
import com.moyorak.api.team.domain.TeamRestaurantSummaries;
import com.moyorak.api.team.dto.TeamRestaurantSummary;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Schema(title = "[파티] 후보 식당 응답 DTO")
public record RestaurantCandidateResponse(
        @Schema(description = "후보 ID", example = "1") Long candidateId,
        @Schema(description = "팀 식당 ID", example = "3") Long teamRestaurantId,
        @Schema(description = "식당 이름", example = "김밥 천국") String restaurantName,
        @Schema(description = "식당 카테고리", example = "KOREAN") RestaurantCategory restaurantCategory,
        @Schema(description = "리뷰 평균 점수", example = "4.3") double averageReviewScore,
        @Schema(description = "리뷰 숫자", example = "50") int reviewCount,
        @Schema(description = "리뷰 이미지 path", example = "https://somepath/review.jpg")
                String reviewImagePath) {
    public static RestaurantCandidateResponse from(
            final Long candidateId,
            final TeamRestaurantSummary teamRestaurantSummary,
            final String reviewImagePath) {
        return new RestaurantCandidateResponse(
                candidateId,
                teamRestaurantSummary.teamRestaurantId(),
                teamRestaurantSummary.restaurantName(),
                teamRestaurantSummary.restaurantCategory(),
                teamRestaurantSummary.averageReviewScore(),
                teamRestaurantSummary.reviewCount(),
                reviewImagePath);
    }

    public static List<RestaurantCandidateResponse> listFrom(
            final TeamRestaurantSummaries teamRestaurantSummaries,
            final FirstReviewPhotoPaths firstReviewPhotoPaths,
            final List<RestaurantCandidate> candidates) {
        final Map<Long, Long> candidateMap =
                candidates.stream()
                        .collect(
                                Collectors.toUnmodifiableMap(
                                        RestaurantCandidate::teamRestaurantId,
                                        RestaurantCandidate::id));

        return teamRestaurantSummaries.getSummaries().stream()
                .map(
                        summary -> {
                            final Long teamRestaurantId1 = summary.teamRestaurantId();
                            return RestaurantCandidateResponse.from(
                                    candidateMap.get(teamRestaurantId1),
                                    summary,
                                    firstReviewPhotoPaths.getPhotoPath(teamRestaurantId1));
                        })
                .toList();
    }
}
