package com.moyorak.api.history.service;

import static org.mockito.BDDMockito.given;

import com.moyorak.api.history.domain.SearchHistory;
import com.moyorak.api.history.domain.SearchHistoryFixture;
import com.moyorak.api.history.dto.SearchHistoryListResponse;
import com.moyorak.api.history.dto.SearchHistoryRequest;
import com.moyorak.api.history.repository.SearchHistoryRepository;
import java.time.LocalDateTime;
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
class SearchHistoryServiceTest {

    @InjectMocks private SearchHistoryService searchHistoryService;

    @Mock private SearchHistoryRepository searchHistoryRepository;

    @Nested
    @DisplayName("팀 맛집 검색 기록 조회 시")
    class GetSearchHistories {

        final Long userId = 1L;
        final Long teamId = 1L;
        final SearchHistoryRequest request = SearchHistoryRequest.create(userId, teamId);

        @Test
        @DisplayName("검색 기록이 최신 순으로 반환된다.")
        void success() {
            // given
            final LocalDateTime now = LocalDateTime.now();
            final SearchHistory history1 =
                    SearchHistoryFixture.fixture(1L, "keyword1", now.minusSeconds(1));
            final SearchHistory history2 = SearchHistoryFixture.fixture(2L, "keyword2", now);

            given(
                            searchHistoryRepository.findAllByUserIdAndTeamIdAndUse(
                                    userId, teamId, true, request.toRecentPageable()))
                    .willReturn(List.of(history2, history1));

            // when
            final SearchHistoryListResponse result =
                    searchHistoryService.getSearchHistories(request);

            // then
            SoftAssertions.assertSoftly(
                    it -> {
                        it.assertThat(result.searchHistories()).hasSize(2);
                        it.assertThat(result.searchHistories())
                                .extracting("keyword")
                                .containsExactly("keyword2", "keyword1");
                    });
        }
    }
}
