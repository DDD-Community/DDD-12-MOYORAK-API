package com.moyorak.api.review.service;

import com.moyorak.api.review.domain.Review;
import com.moyorak.api.review.domain.ReviewServingTime;
import com.moyorak.api.review.domain.ReviewWaitingTime;
import com.moyorak.api.review.dto.ReviewSaveRequest;
import com.moyorak.api.team.domain.TeamRestaurant;
import com.moyorak.api.team.service.TeamRestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewFacade {
    private final ReviewService reviewService;
    private final ReviewPhotoService reviewPhotoService;
    private final TeamRestaurantService teamRestaurantService;

    @Transactional
    public void createReview(
            final Long teamId,
            final Long teamRestaurantId,
            final ReviewSaveRequest reviewSaveRequest) {
        final ReviewServingTime reviewServingTime =
                reviewService.getReviewServingTime(reviewSaveRequest.servingTimeId());
        final ReviewWaitingTime reviewWaitingTime =
                reviewService.getReviewWaitingTime(reviewSaveRequest.waitingTimeId());

        // 유효성 체크 및 조회
        final TeamRestaurant teamRestaurant =
                teamRestaurantService.getValidatedTeamRestaurant(teamId, teamRestaurantId);

        // 리뷰 생성
        final Review review =
                reviewService.crateReview(
                        reviewSaveRequest,
                        reviewServingTime.getServingTimeValue(),
                        reviewWaitingTime.getWaitingTimeValue(),
                        teamRestaurantId);

        // 리뷰 사진 등록
        reviewPhotoService.createReviewPhoto(reviewSaveRequest.photoPaths(), review.getId());

        // 리뷰 등록 시 팀 맛집 평균 값 업데이트
        // 전체 리뷰 갯수 증가
        teamRestaurant.updateReviewCount();
        // 평균 값 업데이트
        teamRestaurant.updateAverageValue(
                review.getScore(),
                reviewServingTime.getServingTimeValue(),
                reviewWaitingTime.getWaitingTimeValue());
    }
}
