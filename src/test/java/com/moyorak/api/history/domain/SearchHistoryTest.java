package com.moyorak.api.history.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SearchHistoryTest {

    @Test
    @DisplayName("delete 메서드는 엔티티 use 필드를 false로 변경한다.")
    void delete() {
        // given
        final SearchHistory searchHistory =
                SearchHistoryFixture.fixture(1L, "우가우가", true, LocalDateTime.now());

        // when
        searchHistory.delete();

        // then
        assertThat(searchHistory.isUse()).isFalse();
    }
}
