package com.moyorak.api.review.service;

import com.moyorak.api.review.domain.Review;
import com.moyorak.api.review.domain.ReviewPhoto;
import com.moyorak.api.review.domain.ReviewServingTime;
import com.moyorak.api.review.domain.ReviewWaitingTime;
import com.moyorak.api.review.dto.ReviewSaveRequest;
import com.moyorak.api.review.dto.ReviewUpdateRequest;
import com.moyorak.api.team.domain.TeamRestaurant;
import com.moyorak.api.team.service.TeamRestaurantSearchService;
import com.moyorak.api.team.service.TeamRestaurantService;
import com.moyorak.config.exception.BusinessException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewFacade {
    private final ReviewService reviewService;
    private final ReviewPhotoService reviewPhotoService;
    private final TeamRestaurantService teamRestaurantService;
    private final TeamRestaurantSearchService teamRestaurantSearchService;

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

        // 전체 리뷰 갯수 증가, 평균 값 업데이트
        teamRestaurantService.updateAverageValue(
                teamRestaurantId,
                review.getScore(),
                reviewServingTime.getServingTimeValue(),
                reviewWaitingTime.getWaitingTimeValue());

        // 업데이트 된 정보 조히 및 검색 테이블에 저장
        final TeamRestaurant updatedTeamRestaurant =
                teamRestaurantService.getValidatedTeamRestaurant(teamId, teamRestaurantId);
        teamRestaurantSearchService.updateAverageReviewScore(
                teamRestaurantId, updatedTeamRestaurant.getAverageReviewScore());
    }

    @Transactional
    public void updateReview(
            final Long teamId,
            final Long teamRestaurantId,
            final Long reviewId,
            final ReviewUpdateRequest reviewUpdateRequest,
            final Long userId) {
        // 유효성 체크
        teamRestaurantService.getValidatedTeamRestaurant(teamId, teamRestaurantId);

        Review review = reviewService.getReview(reviewId);

        if (!review.isMine(userId)) {
            throw new BusinessException("본인 리뷰가 아닙니다.");
        }
        final ReviewServingTime reviewServingTime =
                reviewService.getReviewServingTime(reviewUpdateRequest.servingTimeId());
        final ReviewWaitingTime reviewWaitingTime =
                reviewService.getReviewWaitingTime(reviewUpdateRequest.waitingTimeId());

        review.updateReview(
                reviewUpdateRequest.extraText(),
                reviewServingTime.getServingTimeValue(),
                reviewWaitingTime.getWaitingTimeValue(),
                reviewUpdateRequest.score());

        // 새로운 사진 경로 추출
        final List<ReviewPhoto> reviewPhotoList =
                reviewPhotoService.getReviewPhotosByReviewId(reviewId);
        final List<String> photoPath = reviewPhotoList.stream().map(ReviewPhoto::getPath).toList();
        final List<String> newPhotoPaths =
                reviewUpdateRequest.photoPaths().stream()
                        .filter(path -> !photoPath.contains(path))
                        .toList();

        // 새로운 리뷰 사진 등록
        reviewPhotoService.createReviewPhoto(newPhotoPaths, reviewId);
        // 팀 맛집 평균 값 수정
        teamRestaurantService.recalculateStatsForUpdatedReview(
                teamRestaurantId,
                review.getScore(),
                reviewUpdateRequest.score(),
                review.getServingTime(),
                reviewServingTime.getServingTimeValue(),
                review.getWaitingTime(),
                reviewWaitingTime.getWaitingTimeValue());

        // 업데이트 된 정보 조히 및 검색 테이블에 저장
        final TeamRestaurant updatedTeamRestaurant =
                teamRestaurantService.getValidatedTeamRestaurant(teamId, teamRestaurantId);
        teamRestaurantSearchService.updateAverageReviewScore(
                teamRestaurantId, updatedTeamRestaurant.getAverageReviewScore());
    }
}
