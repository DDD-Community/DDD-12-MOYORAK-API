package com.moyorak.api.history.dto;

import com.moyorak.api.restaurant.domain.RestaurantCategory;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "팀 맛집 상세 조회 기록 응답 DTO")
public record ViewHistoryResponse(
        @Schema(description = "조회 기록 ID", example = "3") Long viewHistoryId,
        @Schema(description = "팀 식당 ID", example = "3") Long teamRestaurantId,
        @Schema(description = "식당 이름", example = "김밥 천국") String restaurantName,
        @Schema(description = "식당 카테고리", example = "KOREAN") RestaurantCategory restaurantCategory,
        @Schema(description = "리뷰 평균 점수", example = "4.3") double averageReviewScore,
        @Schema(description = "리뷰 숫자", example = "50") int reviewCount,
        @Schema(description = "리뷰 이미지 path", example = "https://somepath/review.jpg")
                String reviewImagePath) {
    public static ViewHistoryResponse create(
            Long viewHistoryId,
            Long teamRestaurantId,
            String restaurantName,
            RestaurantCategory restaurantCategory,
            Double averageReviewScore,
            int reviewCount,
            String reviewImagePath) {
        return new ViewHistoryResponse(
                viewHistoryId,
                teamRestaurantId,
                restaurantName,
                restaurantCategory,
                averageReviewScore,
                reviewCount,
                reviewImagePath);
    }
}
