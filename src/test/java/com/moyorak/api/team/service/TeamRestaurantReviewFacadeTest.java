package com.moyorak.api.team.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.moyorak.api.restaurant.domain.Restaurant;
import com.moyorak.api.restaurant.domain.RestaurantCategory;
import com.moyorak.api.restaurant.domain.RestaurantFixture;
import com.moyorak.api.review.domain.ReviewPhotoPaths;
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
import org.assertj.core.api.SoftAssertions;
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
            final ReviewPhotoPaths reviewPhotoPaths = ReviewPhotoPaths.create(photoPaths);

            given(reviewPhotoService.getReviewPhotoPathsGroupedByReviewId(reviewIds))
                    .willReturn(reviewPhotoPaths);

            // when
            final ListResponse<TeamRestaurantReviewResponse> result =
                    teamRestaurantReviewFacade.getTeamRestaurantReviews(
                            teamId, teamRestaurantId, request);

            // then
            assertThat(result.getData()).hasSize(1);
            final TeamRestaurantReviewResponse teamRestaurantReviewResponse =
                    result.getData().getFirst();
            assertThat(teamRestaurantReviewResponse.id()).isEqualTo(1L);
            assertThat(teamRestaurantReviewResponse.photoUrls())
                    .contains("s3://review1/photo1.jpg");
        }
    }

    @Nested
    @DisplayName("팀 맛집 리뷰 사진 조회 시")
    class GetReviewPhotos {

        @Test
        @DisplayName("성공하면 리뷰 사진 경로들을 반환한다")
        void getTeamRestaurantReviewPhotosSuccess() {
            final TeamRestaurantReviewPhotoRequest teamRestaurantReviewPhotoRequest =
                    TeamRestaurantReviewPhotoRequestFixture.fixture(1, 10);

            // given
            given(teamRestaurantService.getValidatedTeamRestaurant(teamId, teamRestaurantId))
                    .willReturn(teamRestaurant);

            final List<String> photoPaths =
                    List.of("s3://review/photo1.jpg", "s3://review/photo2.jpg");

            given(
                            reviewPhotoService.getAllReviewPhotoPathsByTeamRestaurantId(
                                    teamRestaurant.getId()))
                    .willReturn(photoPaths);

            // when
            final ListResponse<PhotoPath> result =
                    teamRestaurantReviewFacade.getTeamRestaurantReviewPhotos(
                            teamId, teamRestaurantId, teamRestaurantReviewPhotoRequest);

            // then
            SoftAssertions.assertSoftly(
                    it -> {
                        it.assertThat(result.getData()).hasSize(2);
                        it.assertThat(result.getData())
                                .containsExactly(
                                        new PhotoPath("s3://review/photo1.jpg"),
                                        new PhotoPath("s3://review/photo2.jpg"));
                    });
        }
    }
}
