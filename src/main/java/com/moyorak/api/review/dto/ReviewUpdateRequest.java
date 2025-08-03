package com.moyorak.api.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record ReviewUpdateRequest(
        @NotNull @Schema(description = "음식 준비 시간 고유 ID", example = "1") Long servingTimeId,
        @NotNull @Schema(description = "대기 시간 고유 ID", example = "1") Long waitingTimeId,
        @NotNull @Schema(description = "평점", example = "1") Integer score,
        @NotNull
                @Size(min = 1, max = 10)
                @Schema(
                        description = "리뷰 사진 들 이미지 경로",
                        example =
                                "[\"https://somepath/review.jpg\", \"https://somepath/review.jpg\"]")
                List<String> photoPaths,
        @NotBlank @Schema(description = "추가 텍스트", example = "여기 참 맛있습니다.") String extraText) {}
