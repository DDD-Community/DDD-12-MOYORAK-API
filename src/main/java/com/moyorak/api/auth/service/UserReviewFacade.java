package com.moyorak.api.auth.service;

import com.moyorak.api.auth.dto.ReviewWithUserAndTeamRestaurantProjection;
import com.moyorak.api.auth.dto.UserReviewRequest;
import com.moyorak.api.auth.dto.UserReviewResponse;
import com.moyorak.api.image.ImageStore;
import com.moyorak.api.review.domain.ReviewPhotoPaths;
import com.moyorak.api.review.domain.ReviewServingTime;
import com.moyorak.api.review.domain.ReviewTimeLabels;
import com.moyorak.api.review.domain.ReviewTimeRangeMapper;
import com.moyorak.api.review.domain.ReviewWaitingTime;
import com.moyorak.api.review.dto.ReviewPhotoPath;
import com.moyorak.api.review.service.ReviewPhotoService;
import com.moyorak.api.review.service.ReviewService;
import com.moyorak.global.domain.ListResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserReviewFacade {
    private final ReviewService reviewService;
    private final ReviewPhotoService reviewPhotoService;
    private final ImageStore imageStore;

    @Transactional(readOnly = true)
    public ListResponse<UserReviewResponse> getReviewWithUserByUserId(
            final Long userId, final UserReviewRequest request) {
        final Page<ReviewWithUserAndTeamRestaurantProjection> reviews =
                reviewService.getReviewWithUserAndTeamRestaurantByUserId(
                        userId, request.toPageableAndDateSorted());

        // 리뷰 Id 추출
        final List<Long> reviewIds =
                reviews.getContent().stream()
                        .map(ReviewWithUserAndTeamRestaurantProjection::id)
                        .toList();

        final List<ReviewServingTime> reviewServingTimes = reviewService.getAllReviewServingTimes();
        final List<ReviewWaitingTime> reviewWaitingTimes = reviewService.getAllReviewWaitingTimes();

        final ReviewTimeRangeMapper reviewTimeRangeMapper =
                ReviewTimeRangeMapper.create(reviewServingTimes, reviewWaitingTimes);
        final ReviewTimeLabels reviewTimeLabels =
                ReviewTimeLabels.createFromUserReview(reviews.getContent(), reviewTimeRangeMapper);
        // 리뷰 별 리뷰 사진들 정보 가져오기
        final List<ReviewPhotoPath> reviewPhotoPathList =
                reviewPhotoService.getReviewPhotoPathsGroupedByReviewId(reviewIds);
        final ReviewPhotoPaths reviewPhotoPaths =
                ReviewPhotoPaths.create(reviewPhotoPathList, imageStore);

        final Page<UserReviewResponse> userReviewResponses =
                UserReviewResponse.from(reviews, reviewPhotoPaths, reviewTimeLabels);
        return ListResponse.from(userReviewResponses);
    }
}
