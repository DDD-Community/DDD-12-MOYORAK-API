package com.moyorak.api.team.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.moyorak.api.restaurant.domain.RestaurantCategory;
import com.moyorak.api.review.domain.FirstReviewPhotoPaths;
import com.moyorak.api.review.dto.FirstReviewPhotoPath;
import com.moyorak.api.review.service.ReviewPhotoService;
import com.moyorak.api.team.domain.SortOption;
import com.moyorak.api.team.domain.TeamRestaurantSummaries;
import com.moyorak.api.team.dto.SearchResult;
import com.moyorak.api.team.dto.TeamRestaurantSearchRequest;
import com.moyorak.api.team.dto.TeamRestaurantSearchRequestFixture;
import com.moyorak.api.team.dto.TeamRestaurantSearchResponse;
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

@ExtendWith(MockitoExtension.class)
class TeamRestaurantSearchFacadeTest {

    @InjectMocks private TeamRestaurantSearchFacade teamRestaurantSearchFacade;

    @Mock private TeamRestaurantSearchService teamRestaurantSearchService;

    @Mock private TeamRestaurantService teamRestaurantService;

    @Mock private ReviewPhotoService reviewPhotoService;

    @Mock private TeamRestaurantEventPublisher teamRestaurantEventPublisher;

    @Nested
    @DisplayName("팀 맛집 검색 시")
    class Search {

        private final Long teamId = 1L;
        private final Long userId = 1L;
        private final Long teamRestaurantId = 10L;
        private final String photoPath = "s3://somepath/review.jpg";

        private final TeamRestaurantSearchRequest request =
                TeamRestaurantSearchRequestFixture.fixture("우가우가", SortOption.DISTANCE, 5, 1);

        @Test
        @DisplayName("리뷰 이미지가 있는 경우 성공적으로 응답을 반환한다")
        void success() {
            // given
            final List<Long> ids = List.of(teamRestaurantId);
            final SearchResult searchResult = new SearchResult(ids, request.toPageable(), 1L);
            final TeamRestaurantSummary teamRestaurantSummary =
                    TeamRestaurantSummaryFixture.fixture(
                            teamRestaurantId, "우가우가", RestaurantCategory.KOREAN, 4.3, 20);

            final FirstReviewPhotoPath photo =
                    new FirstReviewPhotoPath(teamRestaurantId, photoPath);
            final TeamRestaurantSearchResponse response =
                    TeamRestaurantSearchResponse.from(teamRestaurantSummary, photoPath);

            given(
                            teamRestaurantSearchService.search(
                                    teamId, request.getKeyword(), request.toPageable()))
                    .willReturn(searchResult);
            given(teamRestaurantService.findByIdsAndUse(ids, true))
                    .willReturn(TeamRestaurantSummaries.create(List.of(teamRestaurantSummary)));
            given(reviewPhotoService.findFirstReviewPhotoPaths(ids))
                    .willReturn(FirstReviewPhotoPaths.create(ids, List.of(photo)));

            // when
            ListResponse<TeamRestaurantSearchResponse> result =
                    teamRestaurantSearchFacade.search(userId, teamId, request);

            // then
            assertThat(result.getData()).hasSize(1);
            final TeamRestaurantSearchResponse item = result.getData().get(0);
            assertThat(item.teamRestaurantId()).isEqualTo(response.teamRestaurantId());
            assertThat(item.reviewImagePath()).isEqualTo(response.reviewImagePath());
        }
    }
}
