package com.moyorak.api.team.dto;

import com.moyorak.api.restaurant.domain.RestaurantCategory;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "[팀 맛집] 팀 맛집 목록 조회 응답 DTO")
public record TeamRestaurantListResponse(
        @Schema(description = "팀 식당 ID", example = "3") Long teamRestaurantId,
        @Schema(description = "식당 이름", example = "김밥 천국") String restaurantName,
        @Schema(description = "식당 카테고리", example = "KOREAN") RestaurantCategory restaurantCategory,
        @Schema(description = "리뷰 평균 점수", example = "4.3") double averageReviewScore,
        @Schema(description = "리뷰 숫자", example = "50") int reviewCount,
        @Schema(description = "리뷰 이미지 path", example = "https://somepath/review.jpg")
                String reviewImagePath) {
    public static TeamRestaurantListResponse from(
            TeamRestaurantSummary teamRestaurantSummary, String reviewImagePath) {
        return new TeamRestaurantListResponse(
                teamRestaurantSummary.teamRestaurantId(),
                teamRestaurantSummary.restaurantName(),
                teamRestaurantSummary.restaurantCategory(),
                teamRestaurantSummary.averageReviewScore(),
                teamRestaurantSummary.reviewCount(),
                reviewImagePath);
    }
}
