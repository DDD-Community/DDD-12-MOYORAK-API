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
                1,
                teamRestaurantId,
                0,
                review.getScore(),
                0,
                reviewServingTime.getServingTimeValue(),
                0,
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

        Review review = reviewService.getReviewForUpdate(reviewId);

        if (!review.isMine(userId)) {
            throw new BusinessException("본인 리뷰가 아닙니다.");
        }
        final ReviewServingTime reviewServingTime =
                reviewService.getReviewServingTime(reviewUpdateRequest.servingTimeId());
        final ReviewWaitingTime reviewWaitingTime =
                reviewService.getReviewWaitingTime(reviewUpdateRequest.waitingTimeId());

        // 새로운 사진 경로 추출
        final List<ReviewPhoto> reviewPhotoList =
                reviewPhotoService.getReviewPhotosByReviewId(reviewId);
        final List<String> existingPaths =
                reviewPhotoList.stream().map(ReviewPhoto::getPath).toList();

        // 새로 들어온 요청 path
        final List<String> requestedPaths = reviewUpdateRequest.photoPaths();

        // 새로 추가해야 할 path
        final List<String> newPhotoPaths =
                requestedPaths.stream().filter(path -> !existingPaths.contains(path)).toList();

        // 요청에는 없고 기존 DB에만 있는 path
        final List<ReviewPhoto> toDeactivate =
                reviewPhotoList.stream()
                        .filter(photo -> !requestedPaths.contains(photo.getPath()))
                        .toList();

        // 새로운 사진 등록
        reviewPhotoService.createReviewPhoto(newPhotoPaths, reviewId);

        // 기존 사진 삭제
        toDeactivate.forEach(ReviewPhoto::toggleUse);

        // 팀 맛집 평균 값 수정
        teamRestaurantService.updateAverageValue(
                0,
                teamRestaurantId,
                review.getScore(),
                reviewUpdateRequest.score(),
                review.getServingTime(),
                reviewServingTime.getServingTimeValue(),
                review.getWaitingTime(),
                reviewWaitingTime.getWaitingTimeValue());

        // 팀 맛집 데이터 업데이트 후 리뷰 업데이트
        review.updateReview(
                reviewUpdateRequest.extraText(),
                reviewServingTime.getServingTimeValue(),
                reviewWaitingTime.getWaitingTimeValue(),
                reviewUpdateRequest.score());

        // 업데이트 된 정보 조히 및 검색 테이블에 저장
        final TeamRestaurant updatedTeamRestaurant =
                teamRestaurantService.getValidatedTeamRestaurant(teamId, teamRestaurantId);
        teamRestaurantSearchService.updateAverageReviewScore(
                teamRestaurantId, updatedTeamRestaurant.getAverageReviewScore());
    }

    @Transactional
    public void deleteReview(
            final Long teamId,
            final Long teamRestaurantId,
            final Long reviewId,
            final Long userId) {
        // 유효성 체크
        teamRestaurantService.getValidatedTeamRestaurant(teamId, teamRestaurantId);
        Review review = reviewService.getReviewForUpdate(reviewId);

        if (!review.isMine(userId)) {
            throw new BusinessException("본인 리뷰가 아닙니다.");
        }
        // 리뷰 상태값 변경
        review.toggleUse();

        // 리뷰 이미지 상태값 변경
        final List<ReviewPhoto> reviewPhotoList =
                reviewPhotoService.getReviewPhotosByReviewId(reviewId);
        reviewPhotoList.forEach(ReviewPhoto::toggleUse);

        // 팀 맛집 평균 값 수정
        teamRestaurantService.updateAverageValue(
                -1,
                teamRestaurantId,
                review.getScore(),
                0,
                review.getServingTime(),
                0,
                review.getWaitingTime(),
                0);

        // 업데이트 된 정보 조히 및 검색 테이블에 저장
        final TeamRestaurant updatedTeamRestaurant =
                teamRestaurantService.getValidatedTeamRestaurant(teamId, teamRestaurantId);
        teamRestaurantSearchService.updateAverageReviewScore(
                teamRestaurantId, updatedTeamRestaurant.getAverageReviewScore());
    }
}
