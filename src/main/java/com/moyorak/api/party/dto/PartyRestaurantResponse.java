package com.moyorak.api.party.dto;

import com.moyorak.api.restaurant.domain.RestaurantCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.stream.Collectors;

@Schema(title = "[파티] 파티 투표 후보 식당 응답 DTO")
public record PartyRestaurantResponse(
        @Schema(description = "파티 후보 식당 이름", example = "음식점") String name,
        @Schema(description = "파티 후보 식당 카테고리", example = "WESTERN")
                RestaurantCategory restaurantCategory,
        @Schema(description = "파티 후보 식당 리뷰 점수", example = "1.0") double reviewScore,
        @Schema(description = "파티 후보 식당 리뷰 갯수", example = "1") Integer reviewCount) {

    public static PartyRestaurantResponse from(
            PartyRestaurantProjection partyRestaurantProjection) {
        return new PartyRestaurantResponse(
                partyRestaurantProjection.name(),
                partyRestaurantProjection.restaurantCategory(),
                partyRestaurantProjection.averageReviewScore(),
                partyRestaurantProjection.reviewCount());
    }

    public static List<PartyRestaurantResponse> fromList(
            List<PartyRestaurantProjection> partyRestaurantProjectionList) {
        return partyRestaurantProjectionList.stream()
                .map(PartyRestaurantResponse::from)
                .collect(Collectors.toList());
    }
}
