package com.moyorak.api.team.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.BDDMockito.given;

import com.moyorak.api.image.ImageStore;
import com.moyorak.api.restaurant.domain.Restaurant;
import com.moyorak.api.restaurant.domain.RestaurantCategory;
import com.moyorak.api.restaurant.domain.RestaurantFixture;
import com.moyorak.api.review.domain.ReviewServingTime;
import com.moyorak.api.review.domain.ReviewServingTimeFixture;
import com.moyorak.api.review.domain.ReviewWaitingTime;
import com.moyorak.api.review.domain.ReviewWaitingTimeFixture;
import com.moyorak.api.review.dto.PhotoPath;
import com.moyorak.api.review.dto.ReviewPhotoPath;
import com.moyorak.api.review.dto.ReviewWithUserProjection;
import com.moyorak.api.review.dto.ReviewWithUserProjectionFixture;
import com.moyorak.api.review.service.ReviewPhotoService;
import com.moyorak.api.review.service.ReviewService;
import com.moyorak.api.team.domain.TeamRestaurant;
import com.moyorak.api.team.domain.TeamRestaurantFixture;
import com.moyorak.api.team.dto.TeamRestaurantReviewPhotoRequest;
import com.moyorak.api.team.dto.TeamRestaurantReviewPhotoRequestFixture;
import com.moyorak.api.team.dto.TeamRestaurantReviewRequest;
import com.moyorak.api.team.dto.TeamRestaurantReviewRequestFixture;
import com.moyorak.api.team.dto.TeamRestaurantReviewResponse;
import com.moyorak.global.domain.ListResponse;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class TeamRestaurantReviewFacadeTest {

    @InjectMocks private TeamRestaurantReviewFacade teamRestaurantReviewFacade;

    @Mock private ReviewService reviewService;

    @Mock private TeamRestaurantService teamRestaurantService;

    @Mock private ReviewPhotoService reviewPhotoService;
    @Mock private ImageStore imageStore;

    Long teamId;
    Long teamRestaurantId;
    Restaurant restaurant;
    TeamRestaurant teamRestaurant;

    @BeforeEach
    void setUp() {
        teamId = 1L;
        teamRestaurantId = 1L;

        restaurant =
                RestaurantFixture.fixture(
                        "http://place.map.kakao.com/000000",
                        "식당",
                        "서울시 어디구",
                        "서울로 456",
                        RestaurantCategory.KOREAN,
                        127.0,
                        37.0);

        teamRestaurant =
                TeamRestaurantFixture.fixture(
                        teamRestaurantId, "팀 식당", 4.0, 5, 5, 5.0, 5, true, teamId, restaurant);
    }

    @Nested
    @DisplayName("팀 맛집 리뷰 조회 시")
    class GetReview {
        @Test
        @DisplayName("성공 하면 리뷰를 반환한다")
        void getTeamRestaurantReviewsSuccess() {
            // given
            final TeamRestaurantReviewRequest request =
                    TeamRestaurantReviewRequestFixture.fixture(1, 10);

            given(teamRestaurantService.getValidatedTeamRestaurant(teamId, teamRestaurantId))
                    .willReturn(teamRestaurant);

            final ReviewWithUserProjection review =
                    ReviewWithUserProjectionFixture.defaultFixture();
            final Page<ReviewWithUserProjection> reviewPage =
                    new PageImpl<>(List.of(review), PageRequest.of(0, 10), 1);

            given(
                            reviewService.getReviewWithUserByTeamRestaurantId(
                                    teamRestaurantId, request.toPageableAndDateSorted()))
                    .willReturn(reviewPage);

            final List<Long> reviewIds = List.of(1L);
            final List<ReviewPhotoPath> photoPaths =
                    List.of(new ReviewPhotoPath(1L, "s3://review1/photo1.jpg"));

            given(reviewPhotoService.getReviewPhotoPathsGroupedByReviewId(reviewIds))
                    .willReturn(photoPaths);
            given(imageStore.getUrlFromStringPath("s3://review1/photo1.jpg"))
                    .willReturn("https://cdn.moyorak.com/review1/photo1.jpg");

            final ReviewServingTime reviewServingTime =
                    ReviewServingTimeFixture.fixture(1L, "5분이내", true, 5);
            final ReviewWaitingTime reviewWaitingTime =
                    ReviewWaitingTimeFixture.fixture(1L, "5분이내", true, 5);

            given(reviewService.getAllReviewServingTimes()).willReturn(List.of(reviewServingTime));
            given(reviewService.getAllReviewWaitingTimes()).willReturn(List.of(reviewWaitingTime));

            // when
            final ListResponse<TeamRestaurantReviewResponse> result =
                    teamRestaurantReviewFacade.getTeamRestaurantReviews(
                            teamId, teamRestaurantId, request);

            // then
            assertThat(result.getData()).hasSize(1);
            final TeamRestaurantReviewResponse teamRestaurantReviewResponse =
                    result.getData().getFirst();

            assertSoftly(
                    it -> {
                        it.assertThat(teamRestaurantReviewResponse.id()).isEqualTo(1L);
                        it.assertThat(teamRestaurantReviewResponse.photoUrls())
                                .doesNotContain("s3://review1/photo1.jpg");
                        it.assertThat(teamRestaurantReviewResponse.servingTime()).isEqualTo("5분이내");
                        it.assertThat(teamRestaurantReviewResponse.waitingTime()).isEqualTo("5분이내");
                    });
        }
    }

    @Nested
    @DisplayName("팀 맛집 리뷰 사진 조회 시")
    class GetReviewPhotos {

        @Test
        @DisplayName("성공하면 리뷰 사진 경로들을 반환한다")
        void getTeamRestaurantReviewPhotosSuccess() {
            // given
            final TeamRestaurantReviewPhotoRequest teamRestaurantReviewPhotoRequest =
                    TeamRestaurantReviewPhotoRequestFixture.fixture(1, 10);

            given(teamRestaurantService.getValidatedTeamRestaurant(teamId, teamRestaurantId))
                    .willReturn(teamRestaurant);

            final List<PhotoPath> photoPathList =
                    List.of(
                            new PhotoPath("https://cdn.moyorak.com/review/photo1.jpg"),
                            new PhotoPath("https://cdn.moyorak.com/review/photo2.jpg"));
            final Page<PhotoPath> photoPaths =
                    new PageImpl<>(photoPathList, PageRequest.of(0, 10), photoPathList.size());

            given(
                            reviewPhotoService.getAllReviewPhotoPathsByTeamRestaurantId(
                                    teamRestaurant.getId(),
                                    teamRestaurantReviewPhotoRequest.toPageableAndDateSorted()))
                    .willReturn(photoPaths);

            // when
            final ListResponse<PhotoPath> result =
                    teamRestaurantReviewFacade.getTeamRestaurantReviewPhotos(
                            teamId, teamRestaurantId, teamRestaurantReviewPhotoRequest);

            // then
            assertSoftly(
                    it -> {
                        it.assertThat(result.getData()).hasSize(2);
                        it.assertThat(result.getData())
                                .containsExactly(
                                        new PhotoPath("https://cdn.moyorak.com/review/photo1.jpg"),
                                        new PhotoPath("https://cdn.moyorak.com/review/photo2.jpg"));
                    });
        }
    }
}
