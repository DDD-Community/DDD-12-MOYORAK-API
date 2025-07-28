package com.moyorak.api.review.dto;

import com.moyorak.api.review.domain.Review;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record ReviewSaveRequest(
        @NotNull @Schema(description = "회원 고유 ID", example = "1") Long userId,
        @NotNull @Schema(description = "음식 준비 시간 고유 ID", example = "1") Long servingTimeId,
        @NotNull @Schema(description = "대기 시간 고유 ID", example = "1") Long waitingTimeId,
        @NotNull @Schema(description = "평점", example = "1") Integer score,
        @NotNull
                @Schema(
                        description = "리뷰 사진 들 이미지 경로",
                        example = "[https://somepath/review.jpg,https://somepath/review.jpg]")
                List<String> photoPaths,
        @NotNull @Schema(description = "추가 텍스트", example = "여기 참 맛있습니다.") String extraText) {

    public Review toReview(
            final Integer servingTime, final Integer waitingTime, final Long teamRestaurantId) {
        return Review.create(score, servingTime, waitingTime, extraText, userId, teamRestaurantId);
    }
}
