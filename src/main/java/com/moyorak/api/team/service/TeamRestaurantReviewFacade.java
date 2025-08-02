package com.moyorak.api.team.service;

import com.moyorak.api.image.ImageStore;
import com.moyorak.api.review.domain.ReviewPhotoPaths;
import com.moyorak.api.review.domain.ReviewServingTime;
import com.moyorak.api.review.domain.ReviewTimeLabels;
import com.moyorak.api.review.domain.ReviewTimeRangeMapper;
import com.moyorak.api.review.domain.ReviewWaitingTime;
import com.moyorak.api.review.dto.PhotoPath;
import com.moyorak.api.review.dto.ReviewPhotoPath;
import com.moyorak.api.review.dto.ReviewWithUserProjection;
import com.moyorak.api.review.service.ReviewPhotoService;
import com.moyorak.api.review.service.ReviewService;
import com.moyorak.api.team.domain.TeamRestaurant;
import com.moyorak.api.team.dto.TeamRestaurantReviewPhotoRequest;
import com.moyorak.api.team.dto.TeamRestaurantReviewRequest;
import com.moyorak.api.team.dto.TeamRestaurantReviewResponse;
import com.moyorak.global.domain.ListResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TeamRestaurantReviewFacade {

    private final ReviewService reviewService;
    private final TeamRestaurantService teamRestaurantService;
    private final ReviewPhotoService reviewPhotoService;
    private final ImageStore imageStore;

    @Transactional(readOnly = true)
    public ListResponse<TeamRestaurantReviewResponse> getTeamRestaurantReviews(
            Long teamId, Long teamRestaurantId, TeamRestaurantReviewRequest request) {
        final TeamRestaurant teamRestaurant =
                teamRestaurantService.getValidatedTeamRestaurant(teamId, teamRestaurantId);
        final Page<ReviewWithUserProjection> reviews =
                reviewService.getReviewWithUserByTeamRestaurantId(
                        teamRestaurant.getId(), request.toPageableAndDateSorted());

        // 리뷰 Id 추출
        final List<Long> reviewIds =
                reviews.getContent().stream().map(ReviewWithUserProjection::id).toList();

        final List<ReviewServingTime> reviewServingTimes = reviewService.getAllReviewServingTimes();
        final List<ReviewWaitingTime> reviewWaitingTimes = reviewService.getAllReviewWaitingTimes();

        final ReviewTimeRangeMapper reviewTimeRangeMapper =
                ReviewTimeRangeMapper.create(reviewServingTimes, reviewWaitingTimes);
        final ReviewTimeLabels reviewTimeLabels =
                ReviewTimeLabels.create(reviews.getContent(), reviewTimeRangeMapper);
        // 리뷰 별 리뷰 사진들 정보 가져오기
        final List<ReviewPhotoPath> reviewPhotoPathList =
                reviewPhotoService.getReviewPhotoPathsGroupedByReviewId(reviewIds);
        final ReviewPhotoPaths reviewPhotoPaths =
                ReviewPhotoPaths.create(reviewPhotoPathList, imageStore);

        final Page<TeamRestaurantReviewResponse> teamRestaurantReviewResponses =
                TeamRestaurantReviewResponse.from(reviews, reviewPhotoPaths, reviewTimeLabels);
        return ListResponse.from(teamRestaurantReviewResponses);
    }

    @Transactional(readOnly = true)
    public ListResponse<PhotoPath> getTeamRestaurantReviewPhotos(
            Long teamId, Long teamRestaurantId, TeamRestaurantReviewPhotoRequest request) {
        final TeamRestaurant teamRestaurant =
                teamRestaurantService.getValidatedTeamRestaurant(teamId, teamRestaurantId);
        final Page<PhotoPath> reviewPhotoPaths =
                reviewPhotoService.getAllReviewPhotoPathsByTeamRestaurantId(
                        teamRestaurant.getId(), request.toPageableAndDateSorted());
        return ListResponse.from(PhotoPath.convertPathUrl(imageStore, reviewPhotoPaths));
    }
}
