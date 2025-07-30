package com.moyorak.api.review.service;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import com.moyorak.api.restaurant.domain.Restaurant;
import com.moyorak.api.restaurant.domain.RestaurantCategory;
import com.moyorak.api.restaurant.domain.RestaurantFixture;
import com.moyorak.api.review.domain.Review;
import com.moyorak.api.review.domain.ReviewFixture;
import com.moyorak.api.review.domain.ReviewServingTime;
import com.moyorak.api.review.domain.ReviewServingTimeFixture;
import com.moyorak.api.review.domain.ReviewWaitingTime;
import com.moyorak.api.review.domain.ReviewWaitingTimeFixture;
import com.moyorak.api.review.dto.ReviewSaveRequest;
import com.moyorak.api.review.dto.ReviewSaveRequestFixture;
import com.moyorak.api.team.domain.TeamRestaurant;
import com.moyorak.api.team.domain.TeamRestaurantFixture;
import com.moyorak.api.team.repository.TeamRestaurantRepository;
import com.moyorak.api.team.service.TeamRestaurantService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReviewFacadeTest {

    @InjectMocks private ReviewFacade reviewFacade;

    @Mock private ReviewService reviewService;
    @Mock private ReviewPhotoService reviewPhotoService;

    @Mock private TeamRestaurantService teamRestaurantService;
    @Mock private TeamRestaurantRepository teamRestaurantRepository;

    @Test
    @DisplayName("리뷰 생성 성공 시, 평균 값 및 사진 등록까지 처리된다")
    void createReview_success() {
        // given
        final Long teamId = 1L;
        final Long reviewId = 1L;
        final Long servingId = 1L;
        final Long waitingId = 1L;
        final Long userId = 1L;
        final Long teamRestaurantId = 10L;
        final int reviewScore = 4;
        final int servingTimeValue = 5;
        final int waitingTimeValue = 6;

        final ReviewSaveRequest request =
                ReviewSaveRequestFixture.fixture(
                        userId,
                        servingId,
                        waitingId,
                        reviewScore,
                        List.of("s3://photo1.jpg", "s3://photo2.jpg"),
                        "맛있다.");

        final ReviewServingTime servingTime =
                ReviewServingTimeFixture.fixture(servingId, "10~15분", true, servingTimeValue);
        final ReviewWaitingTime waitingTime =
                ReviewWaitingTimeFixture.fixture(waitingId, "10~15분", true, waitingTimeValue);
        final Review review =
                ReviewFixture.fixture(
                        reviewId,
                        reviewScore,
                        servingTimeValue,
                        waitingTimeValue,
                        "맛있다",
                        userId,
                        teamRestaurantId);
        final Restaurant restaurant =
                RestaurantFixture.fixture(
                        "http://place.map.kakao.com/123456",
                        "우가우가 차차차",
                        "우가우가시 차차차동 24번길",
                        "우가우가 차차로 123",
                        RestaurantCategory.KOREAN,
                        127.043616,
                        37.503095);
        final TeamRestaurant teamRestaurant =
                TeamRestaurantFixture.fixture(
                        teamRestaurantId, "맛있네요", 4.5, 5, 5, 5.5, 5, true, teamId, restaurant);

        final Integer beforeReviewCount = teamRestaurant.getReviewCount();
        final double beforeAverageReviewScore = teamRestaurant.getAverageReviewScore();
        final double beforeAverageServingTime = teamRestaurant.getAverageServingTime();
        final double beforeAverageWaitingTime = teamRestaurant.getAverageWaitingTime();

        given(reviewService.getReviewServingTime(servingId)).willReturn(servingTime);
        given(reviewService.getReviewWaitingTime(waitingId)).willReturn(waitingTime);

        given(teamRestaurantService.getValidatedTeamRestaurant(teamId, teamRestaurantId))
                .willReturn(teamRestaurant);

        given(
                        reviewService.crateReview(
                                request, servingTimeValue, waitingTimeValue, teamRestaurantId))
                .willReturn(review);

        // when
        reviewFacade.createReview(teamId, teamRestaurantId, request);

        // then
        verify(reviewPhotoService)
                .createReviewPhoto(List.of("s3://photo1.jpg", "s3://photo2.jpg"), reviewId);
        verify(teamRestaurantService)
                .updateAverageValue(
                        teamRestaurantId, reviewScore, servingTimeValue, waitingTimeValue);
    }
}
