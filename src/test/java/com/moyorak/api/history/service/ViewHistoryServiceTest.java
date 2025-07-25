package com.moyorak.api.history.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.moyorak.api.history.domain.ViewHistory;
import com.moyorak.api.history.domain.ViewHistoryFixture;
import com.moyorak.api.history.dto.ViewHistoryRequest;
import com.moyorak.api.history.dto.ViewHistorySummaries;
import com.moyorak.api.history.repository.ViewHistoryRepository;
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
class ViewHistoryServiceTest {

    @InjectMocks private ViewHistoryService viewHistoryService;

    @Mock private ViewHistoryRepository viewHistoryRepository;

    @Nested
    @DisplayName("팀 맛집 뷰 기록 조회 시")
    class GetViewHistories {

        final Long userId = 1L;
        final Long teamId = 1L;
        final ViewHistoryRequest request = ViewHistoryRequest.create(userId, teamId);

        @Test
        @DisplayName("뷰 기록이 최신 순으로 반환된다.")
        void success() {
            // given
            final LocalDateTime now = LocalDateTime.now();
            final ViewHistory history1 =
                    ViewHistoryFixture.fixture(1L, teamId, userId, 1L, now.minusSeconds(1));
            final ViewHistory history2 = ViewHistoryFixture.fixture(2L, teamId, userId, 2L, now);

            given(
                            viewHistoryRepository.findAllByUserIdAndTeamIdAndUse(
                                    userId, teamId, true, request.toRecentPageable()))
                    .willReturn(List.of(history2, history1));

            // when
            final ViewHistorySummaries result = viewHistoryService.getViewHistorySummaries(request);

            // then
            SoftAssertions.assertSoftly(
                    it -> {
                        it.assertThat(result.summaries()).hasSize(2);
                        it.assertThat(result.summaries()).extracting("id").containsExactly(2L, 1L);
                    });
        }
    }

    @Nested
    @DisplayName("팀 맛집 조회 기록 삭제 시")
    class DeleteViewHistory {
        final Long userId = 1L;
        final Long viewHistoryId = 1L;

        @Test
        @DisplayName("성공한다.")
        void success() {
            // given
            final ViewHistory viewHistory = ViewHistoryFixture.fixture(userId, true);

            given(viewHistoryRepository.findByIdAndUserIdAndUse(viewHistoryId, userId, true))
                    .willReturn(Optional.of(viewHistory));

            // when
            viewHistoryService.deleteViewHistory(userId, viewHistoryId);

            // then
            assertThat(viewHistory.isUse()).isFalse();
        }

        @Test
        @DisplayName("조회 기록이 없으면 예외를 반환한다.")
        void noView() {
            // given
            given(viewHistoryRepository.findByIdAndUserIdAndUse(viewHistoryId, userId, true))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> viewHistoryService.deleteViewHistory(userId, viewHistoryId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("조회 기록이 존재하지 않습니다.");
        }
    }
}
