package com.moyorak.api.party.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class VoteTest {

    @Nested
    @DisplayName("changeStatusByNowForSelectionVote 메서드는")
    class ChangeStatusByNowForSelectionVote {

        final LocalDateTime start = LocalDateTime.of(2025, 8, 14, 12, 0, 0);
        final LocalDateTime end = LocalDateTime.of(2025, 8, 14, 13, 0, 0);
        final Long partyId = 1L;

        @Test
        @DisplayName("now가 시작 전이면 READY로 변경한다.")
        void isReady() {
            // given
            final LocalDateTime now = LocalDateTime.of(2025, 8, 14, 11, 59, 59);
            final Vote vote =
                    VoteFixture.fixture(VoteType.SELECT, VoteStatus.VOTING, true, partyId);
            final SelectionVoteInfo info = SelectionVoteInfoFixture.fixture(start, end);

            // when
            vote.changeStatusByNowForSelectionVote(now, info);

            // then
            assertThat(vote.getStatus()).isEqualTo(VoteStatus.READY);
        }

        @Test
        @DisplayName("now가 시작 이상 만료 전이면 VOTING으로 변경한다.")
        void isVoting() {
            // given
            final LocalDateTime start = LocalDateTime.of(2025, 8, 14, 12, 0, 0);
            final LocalDateTime end = LocalDateTime.of(2025, 8, 14, 13, 0, 0);
            final LocalDateTime now = LocalDateTime.of(2025, 8, 14, 12, 30, 0);

            final Vote vote =
                    VoteFixture.fixture(VoteType.SELECT, VoteStatus.VOTING, true, partyId);
            final SelectionVoteInfo info = SelectionVoteInfoFixture.fixture(start, end);

            // when
            vote.changeStatusByNowForSelectionVote(now, info);

            // then
            assertThat(vote.getStatus()).isEqualTo(VoteStatus.VOTING);
        }

        @Test
        @DisplayName("now가 만료 이상이면 DONE으로 변경한다.")
        void isDone() {
            // given
            final LocalDateTime start = LocalDateTime.of(2025, 8, 14, 12, 0, 0);
            final LocalDateTime end = LocalDateTime.of(2025, 8, 14, 13, 0, 0);
            final LocalDateTime now = LocalDateTime.of(2025, 8, 14, 13, 0, 0);

            final Vote vote =
                    VoteFixture.fixture(VoteType.SELECT, VoteStatus.VOTING, true, partyId);
            final SelectionVoteInfo info = SelectionVoteInfoFixture.fixture(start, end);

            // when
            vote.changeStatusByNowForSelectionVote(now, info);

            // then
            assertThat(vote.getStatus()).isEqualTo(VoteStatus.DONE);
        }
    }

    @Nested
    @DisplayName("changeStatusByNowForRandomVote 메서드는")
    class ChangeStatusByNowForRandomVote {

        final LocalDateTime randomDate = LocalDateTime.of(2025, 8, 14, 12, 0, 0);
        final Long partyId = 1L;

        @Test
        @DisplayName("now가 randomDate 이전이면 READY로 변경한다.")
        void isReady() {
            // given
            final LocalDateTime now = LocalDateTime.of(2025, 8, 14, 11, 59, 59);

            final Vote vote =
                    VoteFixture.fixture(VoteType.SELECT, VoteStatus.VOTING, true, partyId);
            final RandomVoteInfo info = RandomVoteInfoFixture.fixture(randomDate);

            // when
            vote.changeStatusByNowForRandomVote(now, info);

            // then
            assertThat(vote.getStatus()).isEqualTo(VoteStatus.READY);
        }

        @Test
        @DisplayName("now가 randomDate 이상이면 DONE으로 변경한다.")
        void isDone() {
            // given
            final LocalDateTime now = LocalDateTime.of(2025, 8, 14, 12, 0, 0); // 경계 시각

            final Vote vote =
                    VoteFixture.fixture(VoteType.SELECT, VoteStatus.VOTING, true, partyId);
            final RandomVoteInfo info = RandomVoteInfoFixture.fixture(randomDate);

            // when
            vote.changeStatusByNowForRandomVote(now, info);

            // then
            assertThat(vote.getStatus()).isEqualTo(VoteStatus.DONE);
        }
    }

    @Nested
    @DisplayName("isDone 메서드는")
    class IsDone {

        final Long partyId = 1L;

        @Test
        @DisplayName("status가 DONE이면 true를 반환한다.")
        void isTrue() {
            // given
            final Vote vote = VoteFixture.fixture(VoteType.SELECT, VoteStatus.DONE, true, partyId);

            // when
            final boolean result = vote.isDone();

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("status가 DONE이 아니면 false를 반환한다.")
        void isFalse() {
            // given
            final Vote vote =
                    VoteFixture.fixture(VoteType.SELECT, VoteStatus.VOTING, true, partyId);

            // when
            final boolean result = vote.isDone();

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("isSelectVote 메서드는")
    class IsSelectVote {

        final Long partyId = 1L;

        @Test
        @DisplayName("type이 SELECT면 true를 반환한다.")
        void isTrue() {
            // given
            final Vote vote =
                    VoteFixture.fixture(VoteType.SELECT, VoteStatus.VOTING, true, partyId);

            // when
            final boolean result = vote.isSelectVote();

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("type이 SELECT가 아니면 false를 반환한다.")
        void isFalse() {
            // given
            final Vote vote =
                    VoteFixture.fixture(VoteType.RANDOM, VoteStatus.VOTING, true, partyId);

            // when
            final boolean result = vote.isSelectVote();

            // then
            assertThat(result).isFalse();
        }
    }
}
