package com.moyorak.api.auth.dto;

import com.moyorak.api.auth.domain.Allergy;
import com.moyorak.api.auth.domain.Dislike;
import com.moyorak.api.auth.domain.MealTagGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "비선호/알러지 음식 조회 응답")
public record MealTagSearchResponse(
        @Schema(description = "비선호 음식") List<Dislike> dislikes,
        @Schema(description = "알러지") List<Allergy> allergies) {
    public static MealTagSearchResponse from(final MealTagGroup mealTagGroup) {
        return new MealTagSearchResponse(mealTagGroup.dislikes(), mealTagGroup.allergies());
    }
}
