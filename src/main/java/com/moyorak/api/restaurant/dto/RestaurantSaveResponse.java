package com.moyorak.api.restaurant.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "[음식점] 음식점 저장 응답 DTO")
public record RestaurantSaveResponse(
        @Schema(description = "식당 ID", example = "3") Long restaurantId) {
    public static RestaurantSaveResponse create(Long restaurantId) {
        return new RestaurantSaveResponse(restaurantId);
    }
}
