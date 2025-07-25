package com.moyorak.api.history.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ViewHistoryTest {

    @Test
    @DisplayName("delete 메서드는 엔티티 use 필드를 false로 변경한다.")
    void delete() {
        // given
        final ViewHistory viewHistory = ViewHistoryFixture.fixture(1L, true);

        // when
        viewHistory.delete();

        // then
        assertThat(viewHistory.isUse()).isFalse();
    }
}
