package com.moyorak.api.team.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;

import com.moyorak.api.restaurant.domain.Restaurant;
import com.moyorak.api.restaurant.domain.RestaurantCategory;
import com.moyorak.api.restaurant.domain.RestaurantFixture;
import com.moyorak.api.review.domain.FirstReviewPhotoPaths;
import com.moyorak.api.review.domain.ReviewServingTime;
import com.moyorak.api.review.domain.ReviewServingTimeFixture;
import com.moyorak.api.review.domain.ReviewWaitingTime;
import com.moyorak.api.review.domain.ReviewWaitingTimeFixture;
import com.moyorak.api.review.dto.FirstReviewPhotoPath;
import com.moyorak.api.review.service.ReviewPhotoService;
import com.moyorak.api.review.service.ReviewService;
import com.moyorak.api.team.domain.SortOption;
import com.moyorak.api.team.domain.TeamRestaurant;
import com.moyorak.api.team.domain.TeamRestaurantFixture;
import com.moyorak.api.team.domain.TeamRestaurantSummaries;
import com.moyorak.api.team.dto.TeamRestaurantListRequest;
import com.moyorak.api.team.dto.TeamRestaurantListRequestFixture;
import com.moyorak.api.team.dto.TeamRestaurantListResponse;
import com.moyorak.api.team.dto.TeamRestaurantResponse;
import com.moyorak.api.team.dto.TeamRestaurantSummary;
import com.moyorak.api.team.dto.TeamRestaurantSummaryFixture;
import com.moyorak.global.domain.ListResponse;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

@ExtendWith(MockitoExtension.class)
class TeamRestaurantFacadeTest {

    @InjectMocks private TeamRestaurantFacade teamRestaurantFacade;

    @Mock private TeamRestaurantService teamRestaurantService;

    @Mock private ReviewPhotoService reviewPhotoService;

    @Mock private ReviewService reviewService;

    @Mock private TeamRestaurantEventPublisher teamRestaurantEventPublisher;

    @Nested
    @DisplayName("팀 맛집 목록 조회 시")
    class GetRestaurants {

        private final Long teamId = 1L;
        private final Long teamRestaurantId = 1L;
        private final String photoPath = "s3://photo.jpg";
        private final TeamRestaurantListRequest request =
                TeamRestaurantListRequestFixture.fixture(SortOption.DISTANCE, 5, 1);

        @Test
        @DisplayName("팀 맛집이 있는 경우 조회가 된다")
        void success() {
            // given
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
            final Page<TeamRestaurant> teamRestaurantPage =
                    new PageImpl<>(List.of(teamRestaurant), request.toPageable(), 1);
            final List<Long> ids = List.of(teamRestaurantId);
            final TeamRestaurantSummary teamRestaurantSummary =
                    TeamRestaurantSummaryFixture.fixture(
                            teamRestaurantId, "우가우가", RestaurantCategory.KOREAN, 4.3, 20);

            final FirstReviewPhotoPath reviewPhoto =
                    new FirstReviewPhotoPath(teamRestaurantId, photoPath);

            final TeamRestaurantListResponse response =
                    TeamRestaurantListResponse.from(teamRestaurantSummary, photoPath);

            given(teamRestaurantService.getTeamRestaurants(teamId, request))
                    .willReturn(teamRestaurantPage);
            given(teamRestaurantService.findByIdsAndUse(ids, true))
                    .willReturn(TeamRestaurantSummaries.create(List.of(teamRestaurantSummary)));
            given(reviewPhotoService.findFirstReviewPhotoPaths(ids))
                    .willReturn(FirstReviewPhotoPaths.create(ids, List.of(reviewPhoto)));

            // when
            final ListResponse<TeamRestaurantListResponse> result =
                    teamRestaurantFacade.getRestaurants(teamId, request);

            // then
            assertThat(result.getData()).hasSize(1);
            TeamRestaurantListResponse item = result.getData().getFirst();
            assertThat(item.teamRestaurantId()).isEqualTo(response.teamRestaurantId());
            assertThat(item.reviewImagePath()).isEqualTo(response.reviewImagePath());
        }
    }

    @Nested
    @DisplayName("팀 맛집 상세 조회 시,")
    class GetTeamRestaurant {
        private final Long teamId = 1L;
        private final Long teamRestaurantId = 1L;
        private final Long userId = 1L;
        private final String photoPath = "s3://somepath/review.jpg";

        @Test
        @DisplayName("정상적으로 조회되면 응답 DTO를 반환합니다.")
        void returnTeamRestaurantResponse() {
            // given
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
            doNothing()
                    .when(teamRestaurantEventPublisher)
                    .publishViewEvent(userId, teamId, teamRestaurantId);

            final FirstReviewPhotoPath photo =
                    new FirstReviewPhotoPath(teamRestaurantId, photoPath);
            final List<Long> ids = List.of(teamRestaurantId);
            given(reviewPhotoService.findFirstReviewPhotoPaths(ids))
                    .willReturn(FirstReviewPhotoPaths.create(ids, List.of(photo)));

            final ReviewServingTime reviewServingTime =
                    ReviewServingTimeFixture.fixture(1L, "5분이내", true, 5);
            final ReviewWaitingTime reviewWaitingTime =
                    ReviewWaitingTimeFixture.fixture(1L, "5분이내", true, 5);

            given(reviewService.getAllReviewServingTimes()).willReturn(List.of(reviewServingTime));
            given(reviewService.getAllReviewWaitingTimes()).willReturn(List.of(reviewWaitingTime));

            // when
            final TeamRestaurantResponse response =
                    teamRestaurantFacade.getTeamRestaurant(userId, teamId, teamRestaurantId);

            // then
            assertSoftly(
                    it -> {
                        it.assertThat(response.name())
                                .isEqualTo(teamRestaurant.getRestaurant().getName());
                        it.assertThat(response.photoPath()).isEqualTo(photoPath);
                        it.assertThat(response.servingTime()).isEqualTo("5분이내");
                        it.assertThat(response.waitingTime()).isEqualTo("5분이내");
                    });
        }
    }
}
