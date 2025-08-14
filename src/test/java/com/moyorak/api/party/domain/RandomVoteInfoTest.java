package com.moyorak.api.party.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class RandomVoteInfoTest {

    @Nested
    @DisplayName("hasSelectedCandidate 메서드는")
    class HasSelectedCandidate {

        final LocalDateTime randomDate = LocalDateTime.of(2025, 8, 14, 12, 0, 0);
        final LocalDateTime mealDate = LocalDateTime.of(2025, 8, 14, 12, 30, 0);
        final Long voteId = 1L;

        @Test
        @DisplayName("selectedCandidateId가 null이면 false를 반환한다.")
        void isFalse() {
            // given
            final RandomVoteInfo info =
                    RandomVoteInfoFixture.fixture(randomDate, mealDate, voteId, true, null);

            // when
            final boolean result = info.hasSelectedCandidate();

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("selectedCandidateId가 존재하면 true를 반환한다.")
        void isTrue() {
            // given
            final RandomVoteInfo info =
                    RandomVoteInfoFixture.fixture(randomDate, mealDate, voteId, true, 123L);

            // when
            final boolean result = info.hasSelectedCandidate();

            // then
            assertThat(result).isTrue();
        }
    }

    @Nested
    @DisplayName("confirmRandomCandidate 메서드는")
    class ConfirmRandomCandidate {

        final LocalDateTime randomDate = LocalDateTime.of(2025, 8, 14, 12, 0, 0);
        final LocalDateTime mealDate = LocalDateTime.of(2025, 8, 14, 12, 30, 0);
        final Long voteId = 1L;

        @Test
        @DisplayName("selectedCandidateId를 전달된 candidateId로 설정한다.")
        void success() {
            // given
            final RandomVoteInfo info =
                    RandomVoteInfoFixture.fixture(randomDate, mealDate, voteId, true, null);

            // when
            info.confirmRandomCandidate(1234L);

            // then
            assertThat(info.getSelectedCandidateId()).isEqualTo(1234L);
            assertThat(info.hasSelectedCandidate()).isTrue();
        }
    }
}
