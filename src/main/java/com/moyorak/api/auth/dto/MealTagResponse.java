package com.moyorak.api.auth.dto;

import com.moyorak.api.auth.domain.MealTagType;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;

public record MealTagResponse(
        @NotEmpty
                @ArraySchema(
                        arraySchema = @Schema(description = "비선호 음식", example = "[\"해삼\",\"고수\"]"),
                        schema = @Schema(example = "해삼"),
                        uniqueItems = true)
                List<String> dislikes,
        @NotEmpty
                @ArraySchema(
                        arraySchema = @Schema(description = "알러지", example = "[\"복숭아\"]"),
                        schema = @Schema(example = "복숭아"),
                        uniqueItems = true)
                List<String> allergies) {
    public static MealTagResponse of(Map<MealTagType, List<String>> map) {
        return new MealTagResponse(
                List.copyOf(map.getOrDefault(MealTagType.DISLIKE, List.of())),
                List.copyOf(map.getOrDefault(MealTagType.ALLERGY, List.of())));
    }
}
