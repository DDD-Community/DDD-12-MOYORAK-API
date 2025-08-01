package com.moyorak.api.team.dto;

import com.moyorak.api.restaurant.domain.Restaurant;
import com.moyorak.api.review.dto.ReviewSaveRequest;
import com.moyorak.api.team.domain.TeamRestaurant;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;

@Schema(title = "[팀 맛집] 팀 맛집 저장 DTO")
public record TeamRestaurantSaveRequest(
        @NotNull @Positive @Schema(description = "식당 ID", example = "1") Long restaurantId,
        @Size(max = 20, message = "한줄 소개는 {max}자 이하여야 합니다.")
                @Schema(description = "한줄 소개", example = "진짜 맛있습니다!")
                String summary,
        @NotNull @Schema(description = "회원 고유 ID", example = "1") Long userId,
        @NotNull @Schema(description = "음식 준비 시간 고유 ID", example = "1") Long servingTimeId,
        @NotNull @Schema(description = "대기 시간 고유 ID", example = "1") Long waitingTimeId,
        @NotNull @Schema(description = "평점", example = "1") Integer score,
        @NotNull
                @Size(min = 1, max = 10)
                @Schema(
                        description = "리뷰 사진 들 이미지 경로",
                        example = "[https://somepath/review.jpg,https://somepath/review.jpg]")
                List<String> photoPaths,
        @NotBlank @Schema(description = "추가 텍스트", example = "여기 참 맛있습니다.") String extraText) {
    public TeamRestaurant toTeamRestaurant(
            Long teamId, Restaurant restaurant, double distanceFromTeam) {
        return TeamRestaurant.create(teamId, restaurant, summary, distanceFromTeam);
    }

    public ReviewSaveRequest toReviewSaveRequest() {
        return ReviewSaveRequest.create(
                userId, servingTimeId, waitingTimeId, score, photoPaths, extraText);
    }
}
