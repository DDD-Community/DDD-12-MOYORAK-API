package com.moyorak.api.review.dto;

import com.moyorak.api.review.domain.ReviewWaitingTime;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.stream.Collectors;

public record ReviewWaitingTimeResponse(
        @Schema(description = "대기 시간 ID", example = "3") Long waitingTimeId,
        @Schema(description = "대기 시간", example = "10~15분 이내") String waitingTime) {

    public static ReviewWaitingTimeResponse from(ReviewWaitingTime reviewWaitingTime) {
        return new ReviewWaitingTimeResponse(
                reviewWaitingTime.getId(), reviewWaitingTime.getWaitingTime());
    }

    public static List<ReviewWaitingTimeResponse> fromList(
            List<ReviewWaitingTime> reviewWaitingTimes) {
        return reviewWaitingTimes.stream()
                .map(ReviewWaitingTimeResponse::from)
                .collect(Collectors.toList());
    }
}
