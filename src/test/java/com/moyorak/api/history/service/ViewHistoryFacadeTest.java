package com.moyorak.api.history.service;

import static org.mockito.BDDMockito.given;

import com.moyorak.api.history.dto.ViewHistoryListResponse;
import com.moyorak.api.history.dto.ViewHistoryRequest;
import com.moyorak.api.history.dto.ViewHistorySummaries;
import com.moyorak.api.history.dto.ViewHistorySummary;
import com.moyorak.api.restaurant.domain.RestaurantCategory;
import com.moyorak.api.review.domain.FirstReviewPhotoPaths;
import com.moyorak.api.review.dto.FirstReviewPhotoPath;
import com.moyorak.api.review.service.ReviewPhotoService;
import com.moyorak.api.team.domain.TeamRestaurantSummaries;
import com.moyorak.api.team.dto.TeamRestaurantSummary;
import com.moyorak.api.team.service.TeamRestaurantService;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ViewHistoryFacadeTest {

    @InjectMocks private ViewHistoryFacade viewHistoryFacade;

    @Mock private ViewHistoryService viewHistoryService;

    @Mock private TeamRestaurantService teamRestaurantService;

    @Mock private ReviewPhotoService reviewPhotoService;

    @Nested
    @DisplayName("팀 맛집 뷰 기록 Facade 조회 시")
    class GetViewHistories {

        final Long userId = 1L;
        final Long teamId = 1L;
        final Long teamRestaurantId1 = 1L;
        final Long teamRestaurantId2 = 2L;
        final ViewHistoryRequest request = ViewHistoryRequest.create(userId, teamId);

        @Test
        @DisplayName("최신 순으로 반환된다.")
        void success() {
            // given
            final ViewHistorySummary summary1 = new ViewHistorySummary(1L, teamRestaurantId1);
            final ViewHistorySummary summary2 = new ViewHistorySummary(2L, teamRestaurantId2);
            final ViewHistorySummaries viewHistorySummaries =
                    new ViewHistorySummaries(List.of(summary2, summary1));

            given(viewHistoryService.getViewHistorySummaries(request))
                    .willReturn(viewHistorySummaries);

            final TeamRestaurantSummary teamRestaurant1 =
                    new TeamRestaurantSummary(
                            teamRestaurantId1, "식당1", RestaurantCategory.KOREAN, 4.5, 10);
            final TeamRestaurantSummary teamRestaurant2 =
                    new TeamRestaurantSummary(
                            teamRestaurantId2, "식당2", RestaurantCategory.CHINESE, 4.0, 8);
            final TeamRestaurantSummaries teamRestaurantSummaries =
                    TeamRestaurantSummaries.create(List.of(teamRestaurant2, teamRestaurant1));

            given(
                            teamRestaurantService.findByIdsAndUse(
                                    viewHistorySummaries.getTeamRestaurantIds(), true))
                    .willReturn(teamRestaurantSummaries);

            final FirstReviewPhotoPaths firstReviewPhotoPaths =
                    FirstReviewPhotoPaths.create(
                            teamRestaurantSummaries.getTeamRestaurantIds(),
                            List.of(
                                    new FirstReviewPhotoPath(teamRestaurantId2, "path2.jpg"),
                                    new FirstReviewPhotoPath(teamRestaurantId1, "path1.jpg")));

            given(
                            reviewPhotoService.findFirstReviewPhotoPaths(
                                    teamRestaurantSummaries.getTeamRestaurantIds()))
                    .willReturn(firstReviewPhotoPaths);

            // when
            final ViewHistoryListResponse result = viewHistoryFacade.getViewHistories(request);

            // then
            SoftAssertions.assertSoftly(
                    it -> {
                        it.assertThat(result.viewHistories()).hasSize(2);
                        it.assertThat(result.viewHistories())
                                .extracting("teamRestaurantId")
                                .containsExactly(2L, 1L);
                    });
        }

        @Test
        @DisplayName("팀 레스토랑 정보가 없으면 디폴트 값으로 반환된다.")
        void returnDefault() {
            // given
            final ViewHistorySummary summary1 = new ViewHistorySummary(1L, teamRestaurantId1);
            final ViewHistorySummary summary2 = new ViewHistorySummary(2L, teamRestaurantId2);
            final ViewHistorySummaries viewHistorySummaries =
                    new ViewHistorySummaries(List.of(summary2, summary1));

            given(viewHistoryService.getViewHistorySummaries(request))
                    .willReturn(viewHistorySummaries);

            // 팀 레스토랑이 1개만 존재할때
            final TeamRestaurantSummaries teamRestaurantSummaries =
                    TeamRestaurantSummaries.create(
                            List.of(
                                    new TeamRestaurantSummary(
                                            teamRestaurantId1,
                                            "식당1",
                                            RestaurantCategory.KOREAN,
                                            4.5,
                                            10)));
            given(
                            teamRestaurantService.findByIdsAndUse(
                                    viewHistorySummaries.getTeamRestaurantIds(), true))
                    .willReturn(teamRestaurantSummaries);

            final FirstReviewPhotoPaths firstReviewPhotoPaths =
                    FirstReviewPhotoPaths.create(
                            teamRestaurantSummaries.getTeamRestaurantIds(), List.of());
            given(
                            reviewPhotoService.findFirstReviewPhotoPaths(
                                    teamRestaurantSummaries.getTeamRestaurantIds()))
                    .willReturn(firstReviewPhotoPaths);

            // when
            final ViewHistoryListResponse result = viewHistoryFacade.getViewHistories(request);

            // then
            SoftAssertions.assertSoftly(
                    it -> {
                        it.assertThat(result.viewHistories()).hasSize(2);
                        it.assertThat(result.viewHistories())
                                .extracting("restaurantName")
                                .containsExactly("", "식당1");

                        it.assertThat(result.viewHistories())
                                .extracting("restaurantCategory")
                                .containsExactly(RestaurantCategory.ETC, RestaurantCategory.KOREAN);

                        it.assertThat(result.viewHistories())
                                .extracting("averageReviewScore")
                                .containsExactly(0.0, 4.5);

                        it.assertThat(result.viewHistories())
                                .extracting("reviewCount")
                                .containsExactly(0, 10);

                        it.assertThat(result.viewHistories())
                                .extracting("reviewImagePath")
                                .containsExactly(null, null);
                    });
        }
    }
}
