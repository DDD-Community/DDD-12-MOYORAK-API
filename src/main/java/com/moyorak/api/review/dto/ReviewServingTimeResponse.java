package com.moyorak.api.review.dto;

import com.moyorak.api.review.domain.ReviewServingTime;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.stream.Collectors;

public record ReviewServingTimeResponse(
        @Schema(description = "서빙 시간 id", example = "3") Long servingTimeId,
        @Schema(description = "서빙 시간", example = "10~15분 이내") String servingTime) {

    public static ReviewServingTimeResponse from(ReviewServingTime reviewServingTime) {
        return new ReviewServingTimeResponse(
                reviewServingTime.getId(), reviewServingTime.getServingTime());
    }

    public static List<ReviewServingTimeResponse> fromList(
            List<ReviewServingTime> reviewServingTimes) {
        return reviewServingTimes.stream()
                .map(ReviewServingTimeResponse::from)
                .collect(Collectors.toList());
    }
}
