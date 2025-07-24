package com.moyorak.api.history.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.moyorak.api.history.domain.SearchHistory;
import com.moyorak.api.history.domain.SearchHistoryFixture;
import com.moyorak.api.history.dto.SearchHistoryListResponse;
import com.moyorak.api.history.dto.SearchHistoryRequest;
import com.moyorak.api.history.repository.SearchHistoryRepository;
import com.moyorak.config.exception.BusinessException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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

    @Nested
    @DisplayName("팀 맛집 검색 기록 삭제 시")
    class DeleteSearchHistory {
        final Long userId = 1L;
        final Long searchHistoryId = 1L;

        @Test
        @DisplayName("성공한다.")
        void success() {
            // given
            final SearchHistory searchHistory =
                    SearchHistoryFixture.fixture(
                            searchHistoryId, "우가우가", userId, true, LocalDateTime.now());

            given(searchHistoryRepository.findByIdAndUserIdAndUse(searchHistoryId, userId, true))
                    .willReturn(Optional.of(searchHistory));

            // when
            searchHistoryService.deleteSearchHistory(searchHistoryId, userId);

            // then
            assertThat(searchHistory.isUse()).isFalse();
        }

        @Test
        @DisplayName("검색 기록이 없으면 예외를 반환한다.")
        void noHistory() {
            // given
            given(searchHistoryRepository.findByIdAndUserIdAndUse(searchHistoryId, userId, true))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(
                            () -> searchHistoryService.deleteSearchHistory(searchHistoryId, userId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("검색 기록이 존재하지 않습니다.");
        }
    }
}
