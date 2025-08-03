package com.moyorak.api.team.service;

import com.moyorak.api.review.domain.FirstReviewPhotoPaths;
import com.moyorak.api.review.domain.Review;
import com.moyorak.api.review.domain.ReviewServingTime;
import com.moyorak.api.review.domain.ReviewTimeRangeMapper;
import com.moyorak.api.review.domain.ReviewWaitingTime;
import com.moyorak.api.review.dto.ReviewSaveRequest;
import com.moyorak.api.review.service.ReviewPhotoService;
import com.moyorak.api.review.service.ReviewService;
import com.moyorak.api.team.domain.TeamRestaurant;
import com.moyorak.api.team.domain.TeamRestaurantSummaries;
import com.moyorak.api.team.dto.ListResult;
import com.moyorak.api.team.dto.TeamRestaurantListRequest;
import com.moyorak.api.team.dto.TeamRestaurantListResponse;
import com.moyorak.api.team.dto.TeamRestaurantResponse;
import com.moyorak.api.team.dto.TeamRestaurantSaveRequest;
import com.moyorak.api.team.dto.TeamRestaurantSaveResponse;
import com.moyorak.global.domain.ListResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TeamRestaurantFacade {

    private final TeamRestaurantService teamRestaurantService;
    private final TeamRestaurantSearchService teamRestaurantSearchService;
    private final ReviewPhotoService reviewPhotoService;
    private final ReviewService reviewService;
    private final TeamRestaurantEventPublisher teamRestaurantEventPublisher;

    @Transactional(readOnly = true)
    public ListResponse<TeamRestaurantListResponse> getRestaurants(
            final Long teamId, final TeamRestaurantListRequest teamRestaurantListRequest) {

        final Page<TeamRestaurant> teamRestaurantPage =
                teamRestaurantService.getTeamRestaurants(teamId, teamRestaurantListRequest);

        List<Long> teamRestaurantIds =
                teamRestaurantPage.getContent().stream().map(TeamRestaurant::getId).toList();
        // 팀 식당 id로 필요한 정보 가져오기
        final TeamRestaurantSummaries teamRestaurantSummaries =
                teamRestaurantService.findByIdsAndUse(teamRestaurantIds, true);

        // 팀 식당별로 리뷰 첫 사진 정보 가져오기
        final FirstReviewPhotoPaths firstReviewPhotoPaths =
                reviewPhotoService.findFirstReviewPhotoPaths(teamRestaurantIds);

        // 팀 식당 정보로 응답 조합
        final List<TeamRestaurantListResponse> teamRestaurantListResponses =
                teamRestaurantSummaries.toListResponse(firstReviewPhotoPaths);

        final ListResult listResult =
                new ListResult(
                        teamRestaurantIds,
                        teamRestaurantListRequest.toPageable(),
                        teamRestaurantPage.getTotalElements());

        return ListResponse.from(listResult.toPage(teamRestaurantListResponses));
    }

    @Transactional(readOnly = true)
    public TeamRestaurantResponse getTeamRestaurant(
            Long userId, Long teamId, Long teamRestaurantId) {
        final TeamRestaurant teamRestaurant =
                teamRestaurantService.getValidatedTeamRestaurant(teamId, teamRestaurantId);

        // 조회 이벤트 발행
        teamRestaurantEventPublisher.publishViewEvent(userId, teamId, teamRestaurantId);

        final FirstReviewPhotoPaths firstReviewPhotoPaths =
                reviewPhotoService.findFirstReviewPhotoPaths(List.of(teamRestaurantId));
        final String photoPath = firstReviewPhotoPaths.getPhotoPath(teamRestaurantId);

        final List<ReviewServingTime> reviewServingTimes = reviewService.getAllReviewServingTimes();
        final List<ReviewWaitingTime> reviewWaitingTimes = reviewService.getAllReviewWaitingTimes();
        final ReviewTimeRangeMapper reviewTimeRangeMapper =
                ReviewTimeRangeMapper.create(reviewServingTimes, reviewWaitingTimes);
        return TeamRestaurantResponse.from(teamRestaurant, photoPath, reviewTimeRangeMapper);
    }

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
                1,
                teamRestaurantId,
                0,
                review.getScore(),
                0,
                reviewServingTime.getServingTimeValue(),
                0,
                reviewWaitingTime.getWaitingTimeValue());

        // 검색에 리뷰 평균 값 업데이트
        final TeamRestaurant teamRestaurant =
                teamRestaurantService.getValidatedTeamRestaurant(teamId, teamRestaurantId);
        teamRestaurantSearchService.updateAverageReviewScore(
                teamRestaurantId, teamRestaurant.getAverageReviewScore());

        return TeamRestaurantSaveResponse.create(teamRestaurantId);
    }
}
