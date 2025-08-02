package com.moyorak.api.team.dto;

import com.moyorak.api.restaurant.domain.RestaurantCategory;
import com.moyorak.api.review.domain.ReviewTimeRangeMapper;
import com.moyorak.api.team.domain.TeamRestaurant;
import io.swagger.v3.oas.annotations.media.Schema;

public record TeamRestaurantResponse(
        @Schema(description = "식당 이름", example = "김밥 천국") String name,
        @Schema(description = "레스토랑 카테고리", example = "한식") RestaurantCategory restaurantCategory,
        @Schema(description = "한줄평", example = "여기 맛있습니다") String summary,
        @Schema(description = "장소 링크 url", example = "https://naver.me/xFLu69C9") String placeUrl,
        @Schema(description = "음식 나오는 시간", example = "5분이내") String servingTime,
        @Schema(description = "대기 시간", example = "5분이내") String waitingTime,
        @Schema(description = "리뷰 갯수", example = "50") Integer reviewCount,
        @Schema(description = "평점", example = "4.5") double score,
        @Schema(description = "사진 경로", example = "https://..") String photoPath) {
    public static TeamRestaurantResponse from(
            TeamRestaurant teamRestaurant,
            String photoPath,
            ReviewTimeRangeMapper reviewTimeRangeMapper) {
        return new TeamRestaurantResponse(
                teamRestaurant.getRestaurant().getName(),
                teamRestaurant.getRestaurant().getCategory(),
                teamRestaurant.getSummary(),
                teamRestaurant.getRestaurant().getPlaceUrl(),
                reviewTimeRangeMapper.mapServingTime(teamRestaurant.getAverageServingTime()),
                reviewTimeRangeMapper.mapWaitingTime(teamRestaurant.getAverageWaitingTime()),
                teamRestaurant.getReviewCount(),
                teamRestaurant.getAverageReviewScore(),
                photoPath);
    }
}
