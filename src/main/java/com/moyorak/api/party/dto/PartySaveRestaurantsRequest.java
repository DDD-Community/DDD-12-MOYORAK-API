package com.moyorak.api.party.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Schema(title = "[파티] 파티 생성 요청 DTO - 투표 식당 정보")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PartySaveRestaurantsRequest {

    @Valid
    @Size(min = 1, max = 5, message = "식당은 1개 이상, 최대 5개 까지 등록 가능합니다.")
    @Schema(description = "선택 식당 고유 ID", example = "1")
    private List<PartySaveRestaurantRequest> ids;
}
