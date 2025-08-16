package com.moyorak.api.party.dto;

import com.moyorak.api.auth.domain.MealTagType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Map;

public record PartyAttendeeListResponse(
        @Schema(description = "유저 고유 ID", example = "1") Long userId,
        @Schema(description = "유저 이름", example = "홍길동") String userName,
        @Schema(description = "유저 프로필 이미지", example = "https://image.image.png")
                String profileImage,
        @Schema(description = "유저 비헌호,알러지 음식", example = "{DISLIKE=[파, 콩나물], ALLERGY=[땅콩]}")
                Map<MealTagType, List<String>> mealTags) {

    public static List<PartyAttendeeListResponse> fromList(
            List<PartyAttendeeWithUserProfile> list,
            Map<Long, Map<MealTagType, List<String>>> map) {
        return list.stream()
                .map(
                        p ->
                                (new PartyAttendeeListResponse(
                                        p.userId(),
                                        p.userName(),
                                        p.userProfile(),
                                        map.get(p.userId()))))
                .toList();
    }
}
