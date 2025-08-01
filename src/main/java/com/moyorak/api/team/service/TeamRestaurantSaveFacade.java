package com.moyorak.api.team.service;

import com.moyorak.api.review.domain.Review;
import com.moyorak.api.review.domain.ReviewServingTime;
import com.moyorak.api.review.domain.ReviewWaitingTime;
import com.moyorak.api.review.dto.ReviewSaveRequest;
import com.moyorak.api.review.service.ReviewPhotoService;
import com.moyorak.api.review.service.ReviewService;
import com.moyorak.api.team.domain.TeamRestaurant;
import com.moyorak.api.team.dto.TeamRestaurantSaveRequest;
import com.moyorak.api.team.dto.TeamRestaurantSaveResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TeamRestaurantSaveFacade {

    private final TeamRestaurantService teamRestaurantService;
    private final TeamRestaurantSearchService teamRestaurantSearchService;
    private final ReviewService reviewService;
    private final ReviewPhotoService reviewPhotoService;

    @Transactional
    public TeamRestaurantSaveResponse save(
            final Long userId,
            final Long teamId,
            final TeamRestaurantSaveRequest teamRestaurantSaveRequest) {

        // 팀 맛집 저장
        Long teamRestaurantId =
                teamRestaurantService.save(userId, teamId, teamRestaurantSaveRequest);

        // 리뷰 저장
        final ReviewSaveRequest reviewSaveRequest = teamRestaurantSaveRequest.toReviewSaveRequest();

        final ReviewServingTime reviewServingTime =
                reviewService.getReviewServingTime(reviewSaveRequest.servingTimeId());
        final ReviewWaitingTime reviewWaitingTime =
                reviewService.getReviewWaitingTime(reviewSaveRequest.waitingTimeId());

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

        // 검색에 리뷰 평균 값 업데이트
        final TeamRestaurant teamRestaurant =
                teamRestaurantService.getValidatedTeamRestaurant(teamId, teamRestaurantId);
        teamRestaurantSearchService.updateAverageReviewScore(
                teamRestaurantId, teamRestaurant.getAverageReviewScore());

        return TeamRestaurantSaveResponse.create(teamRestaurantId);
    }
}
