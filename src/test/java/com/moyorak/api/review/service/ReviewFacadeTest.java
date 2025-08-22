package com.moyorak.api.review.service;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import com.moyorak.api.restaurant.domain.Restaurant;
import com.moyorak.api.restaurant.domain.RestaurantCategory;
import com.moyorak.api.restaurant.domain.RestaurantFixture;
import com.moyorak.api.review.domain.Review;
import com.moyorak.api.review.domain.ReviewFixture;
import com.moyorak.api.review.domain.ReviewPhoto;
import com.moyorak.api.review.domain.ReviewPhotoFixture;
import com.moyorak.api.review.domain.ReviewServingTime;
import com.moyorak.api.review.domain.ReviewServingTimeFixture;
import com.moyorak.api.review.domain.ReviewWaitingTime;
import com.moyorak.api.review.domain.ReviewWaitingTimeFixture;
import com.moyorak.api.review.dto.ReviewSaveRequest;
import com.moyorak.api.review.dto.ReviewSaveRequestFixture;
import com.moyorak.api.review.dto.ReviewUpdateRequest;
import com.moyorak.api.review.dto.ReviewUpdateRequestFixture;
import com.moyorak.api.team.domain.TeamRestaurant;
import com.moyorak.api.team.domain.TeamRestaurantFixture;
import com.moyorak.api.team.service.TeamRestaurantSearchService;
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
    @Mock private TeamRestaurantSearchService teamRestaurantSearchService;

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
                        1,
                        teamRestaurantId,
                        0,
                        reviewScore,
                        0,
                        servingTimeValue,
                        0,
                        waitingTimeValue);

        verify(teamRestaurantService, times(2))
                .getValidatedTeamRestaurant(teamId, teamRestaurantId); // 총 2회 호출됨

        verify(teamRestaurantSearchService)
                .updateAverageReviewScore(teamRestaurantId, teamRestaurant.getAverageReviewScore());
    }

    @Test
    @DisplayName("리뷰 수정 성공 시, 사진 추가 및 평균값이 재계산되어야 한다")
    void updateReview_success() {
        // given
        final Long teamId = 1L;
        final Long teamRestaurantId = 10L;
        final Long reviewId = 100L;
        final Long newReviewServingTimeId = 100L;
        final Long newReviewWaitingTimeId = 100L;
        final Long userId = 999L;

        final int oldScore = 3;
        final int newScore = 4;

        final int oldServingTime = 5;
        final int newServingTime = 10;

        final int oldWaitingTime = 7;
        final int newWaitingTime = 8;

        final ReviewUpdateRequest request =
                ReviewUpdateRequestFixture.fixture(
                        newReviewServingTimeId,
                        newReviewWaitingTimeId,
                        newScore,
                        List.of("s3://new1.jpg", "s3://new2.jpg"),
                        "업데이트 내용");

        final Review review =
                ReviewFixture.fixture(
                        reviewId,
                        oldScore,
                        oldServingTime,
                        oldWaitingTime,
                        "맛있다",
                        userId,
                        teamRestaurantId);

        final ReviewServingTime servingTime =
                ReviewServingTimeFixture.fixture(
                        newReviewServingTimeId, "10~15분", true, newServingTime);
        final ReviewWaitingTime waitingTime =
                ReviewWaitingTimeFixture.fixture(
                        newReviewWaitingTimeId, "5~10분", true, newWaitingTime);

        final List<ReviewPhoto> existingPhotos =
                List.of(
                        ReviewPhotoFixture.fixture(1L, "s3://old1.jpg", true, reviewId),
                        ReviewPhotoFixture.fixture(2L, "s3://old2.jpg", true, reviewId));
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

        given(teamRestaurantService.getValidatedTeamRestaurant(teamId, teamRestaurantId))
                .willReturn(teamRestaurant);
        given(reviewService.getReviewForUpdate(reviewId)).willReturn(review);
        given(reviewService.getReviewServingTime(request.servingTimeId())).willReturn(servingTime);
        given(reviewService.getReviewWaitingTime(request.waitingTimeId())).willReturn(waitingTime);
        given(reviewPhotoService.getReviewPhotosByReviewId(reviewId)).willReturn(existingPhotos);

        // when
        reviewFacade.updateReview(teamId, teamRestaurantId, reviewId, request, userId);

        // then
        verify(reviewService).getReviewForUpdate(reviewId);

        verify(reviewPhotoService)
                .createReviewPhoto(List.of("s3://new1.jpg", "s3://new2.jpg"), reviewId);

        // 동작 확인
        verify(teamRestaurantService)
                .updateAverageValue(
                        eq(0),
                        eq(teamRestaurantId),
                        anyInt(),
                        eq(newScore),
                        anyInt(),
                        eq(newServingTime),
                        anyInt(),
                        eq(newWaitingTime));

        verify(teamRestaurantService, times(2))
                .getValidatedTeamRestaurant(teamId, teamRestaurantId);

        verify(teamRestaurantSearchService)
                .updateAverageReviewScore(teamRestaurantId, teamRestaurant.getAverageReviewScore());
    }

    @DisplayName("리뷰 삭제 성공 시, 상태 변경 및 평균값이 재계산되어야 한다")
    @Test
    void deleteReview_success() {
        // given
        final Long teamId = 1L;
        final Long teamRestaurantId = 10L;
        final Long reviewId = 1L;
        final Long userId = 9L;

        final int score = 4;
        final int servingTime = 10;
        final int waitingTime = 8;

        final Review review =
                ReviewFixture.fixture(
                        reviewId,
                        score,
                        servingTime,
                        waitingTime,
                        "맛있어요!",
                        userId,
                        teamRestaurantId);

        final Restaurant restaurant =
                RestaurantFixture.fixture(
                        "http://place.map.kakao.com/123456",
                        "테스트 식당",
                        "서울시 어딘가",
                        "서울시 도로명 주소",
                        RestaurantCategory.KOREAN,
                        127.0,
                        37.0);

        final TeamRestaurant teamRestaurant =
                TeamRestaurantFixture.fixture(
                        teamRestaurantId, "맛집", 4.5, 5, 5, 5.5, 5, true, teamId, restaurant);

        final List<ReviewPhoto> reviewPhotos =
                List.of(
                        ReviewPhotoFixture.fixture(1L, "s3://photo1.jpg", true, reviewId),
                        ReviewPhotoFixture.fixture(2L, "s3://photo2.jpg", true, reviewId));

        given(teamRestaurantService.getValidatedTeamRestaurant(teamId, teamRestaurantId))
                .willReturn(teamRestaurant);
        given(reviewService.getReviewForUpdate(reviewId)).willReturn(review);
        given(reviewPhotoService.getReviewPhotosByReviewId(reviewId)).willReturn(reviewPhotos);

        // when
        reviewFacade.deleteReview(teamId, teamRestaurantId, reviewId, userId);

        // then
        verify(teamRestaurantService, times(2))
                .getValidatedTeamRestaurant(teamId, teamRestaurantId);
        verify(reviewService).getReviewForUpdate(reviewId);
        verify(reviewPhotoService).getReviewPhotosByReviewId(reviewId);

        verify(teamRestaurantService)
                .updateAverageValue(
                        eq(-1),
                        eq(teamRestaurantId),
                        eq(score),
                        eq(0),
                        eq(servingTime),
                        eq(0),
                        eq(waitingTime),
                        eq(0));

        verify(teamRestaurantSearchService)
                .updateAverageReviewScore(teamRestaurantId, teamRestaurant.getAverageReviewScore());
    }
}
