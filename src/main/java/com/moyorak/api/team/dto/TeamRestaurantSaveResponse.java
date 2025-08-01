package com.moyorak.api.team.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "[팀 맛집] 팀 맛집 저장 응답 DTO")
public record TeamRestaurantSaveResponse(
        @Schema(description = "팀 식당 ID", example = "1") Long teamRestaurantId) {
    public static TeamRestaurantSaveResponse create(final Long teamRestaurantId) {
        return new TeamRestaurantSaveResponse(teamRestaurantId);
    }
}
