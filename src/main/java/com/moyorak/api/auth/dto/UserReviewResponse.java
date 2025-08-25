package com.moyorak.api.auth.dto;

import com.moyorak.api.review.domain.ReviewPhotoPaths;
import com.moyorak.api.review.domain.ReviewTimeLabels;
import com.moyorak.api.review.dto.ReviewWithUserProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.springframework.data.domain.Page;

@Schema(title = "[마이] 사용자 리뷰 조회 응답 DTO")
public record UserReviewResponse(
        @Schema(description = "리뷰 ID", example = "1") Long id,
        @Schema(description = "추가 텍스트", example = "음식이 조금 짰지만 전체적으로 만족스러웠습니다.") String extraText,
        @Schema(description = "리뷰 점수 (1~5)", example = "4") Integer score,
        @Schema(description = "서빙 시간", example = "바로 준비됌") String servingTime,
        @Schema(description = "대기 시간", example = "대기시간 없음") String waitingTime,
        @Schema(description = "작성자 사용자 ID", example = "1") Long userId,
        @Schema(description = "작성자 닉네임", example = "맛잘알") String userNickname,
        @Schema(description = "작성자 프로필 이미지 URL", example = "https://example.com/profile/1001.png")
                String userProfileImageUrl,
        @Schema(
                        description = "리뷰 사진 URL 응답 리스트",
                        example = "[\"https://example.com/review/1.png\"]")
                List<String> photoUrls,
        @Schema(description = "리뷰 작성일 (yyyy-MM-dd)", example = "2025-08-25") String createdDate) {
    public static Page<UserReviewResponse> from(
            Page<ReviewWithUserProjection> review,
            ReviewPhotoPaths reviewPhotoPaths,
            ReviewTimeLabels reviewTimeLabels) {
        return review.map(
                r ->
                        new UserReviewResponse(
                                r.id(),
                                r.extraText(),
                                r.score(),
                                reviewTimeLabels.getServingLabel(r.id()),
                                reviewTimeLabels.getWaitingLabel(r.id()),
                                r.userId(),
                                r.name(),
                                r.profileImage(),
                                reviewPhotoPaths.getPhotoPaths(r.id()),
                                reviewTimeLabels.convertDate(r.createdDate().toLocalDate())));
    }
}
