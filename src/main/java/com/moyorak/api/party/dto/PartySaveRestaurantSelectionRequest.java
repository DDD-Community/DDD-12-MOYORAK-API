package com.moyorak.api.party.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Schema(title = "[파티] 파티 생성 요청 DTO - 식당 정보")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PartySaveRestaurantSelectionRequest {

    @Valid
    @Schema(description = "투표할 식당 정보")
    private PartySaveRestaurantsRequest restaurants;
}
