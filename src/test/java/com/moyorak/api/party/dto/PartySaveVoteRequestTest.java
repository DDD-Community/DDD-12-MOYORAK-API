package com.moyorak.api.party.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.moyorak.api.party.domain.VoteType;
import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class PartySaveVoteRequestTest {

    @Nested
    @DisplayName("투표 방식이 선택(Select)일 때, 시작 시간이 필수임을 판단할 때,")
    class isValidStartTime {

        @Test
        @DisplayName("투표 방식이 null이면 false를 반환합니다.")
        void isNull() {
            // given
            final PartySaveVoteRequest request =
                    PartySaveVoteRequestFixture.fixture(null, null, null, null);

            // when
            final Boolean result = request.isValidStartTime();

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("투표 방식이 랜덤(Random)이면 무조건 true를 반환합니다.")
        void isNotSelect() {
            // given
            final VoteType voteType = VoteType.RANDOM;
            final PartySaveVoteRequest request =
                    PartySaveVoteRequestFixture.fixture(voteType, null, null, null);

            // when
            final Boolean result = request.isValidStartTime();

            // then
            assertThat(result).isTrue();
        }

        @Nested
        @DisplayName("투표 방식이 선택(Select)일 때,")
        class select {

            @Test
            @DisplayName("시작 시간이 있으면 true가 반환됩니다.")
            void isTrue() {
                // given
                final VoteType voteType = VoteType.SELECT;
                final LocalTime fromTime = LocalTime.now();
                final PartySaveVoteRequest request =
                        PartySaveVoteRequestFixture.fixture(voteType, fromTime, null, null);

                // when
                final Boolean result = request.isValidStartTime();

                // then
                assertThat(result).isTrue();
            }

            @Test
            @DisplayName("시작 시간이 없으면 false가 반환됩니다.")
            void isFalse() {
                // given
                final VoteType voteType = VoteType.SELECT;
                final LocalTime fromTime = null;
                final PartySaveVoteRequest request =
                        PartySaveVoteRequestFixture.fixture(voteType, fromTime, null, null);

                // when
                final Boolean result = request.isValidStartTime();

                // then
                assertThat(result).isFalse();
            }
        }
    }

    @Nested
    @DisplayName("시작 시간이 종료 시간보다 빠른지 비교할 때,")
    class isValidTimeRange {

        @ParameterizedTest
        @ValueSource(ints = {13, 14})
        @DisplayName("빠르거나 같다면 true를 반환합니다.")
        void isTrue(final Integer hour) {
            // given
            final LocalTime fromTime = LocalTime.of(hour, 0, 0);
            final LocalTime toTime = LocalTime.of(14, 0, 0);
            final PartySaveVoteRequest request =
                    PartySaveVoteRequestFixture.fixture(VoteType.RANDOM, fromTime, toTime, null);

            // when
            final Boolean result = request.isValidTimeRange();

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("느리다면 false를 반환합니다.")
        void isFalse() {
            // given
            final LocalTime fromTime = LocalTime.of(15, 0, 0);
            final LocalTime toTime = LocalTime.of(14, 0, 0);
            final PartySaveVoteRequest request =
                    PartySaveVoteRequestFixture.fixture(VoteType.RANDOM, fromTime, toTime, null);

            // when
            final Boolean result = request.isValidTimeRange();

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("투표 시작 시간, 종료 시간은 식사 시간 전을 비교할 때,")
    class isMealTimeAfter {

        @Test
        @DisplayName("식사 시간이 null이면, false를 반환합니다.")
        void mealTimeIsNull() {
            // given
            final LocalTime mealTime = null;

            final PartySaveVoteRequest request =
                    PartySaveVoteRequestFixture.fixture(null, null, LocalTime.now(), mealTime);

            // when
            final Boolean result = request.isMealTimeAfter();

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("종료 시간이 null이면, false를 반환합니다.")
        void toTimeIsNull() {
            // given
            final LocalTime toTime = null;

            final PartySaveVoteRequest request =
                    PartySaveVoteRequestFixture.fixture(null, null, toTime, LocalTime.now());

            // when
            final Boolean result = request.isMealTimeAfter();

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("투표 시작 시간이 식사 시간보다 이후면 false를 반환합니다.")
        void isOverFromTime() {
            // given
            final LocalTime mealTime = LocalTime.of(11, 0, 0);
            final LocalTime fromTime = mealTime.plusHours(1);
            final LocalTime toTime = LocalTime.of(10, 0, 0);

            final PartySaveVoteRequest request =
                    PartySaveVoteRequestFixture.fixture(null, fromTime, toTime, mealTime);

            // when
            final Boolean result = request.isMealTimeAfter();

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("투표 종료 시간이 식사 시간보다 이후면 false를 반환합니다.")
        void isOverToTime() {
            // given
            final LocalTime mealTime = LocalTime.of(11, 0, 0);
            final LocalTime fromTime = LocalTime.of(10, 0, 0);
            final LocalTime toTime = mealTime.plusHours(1);

            final PartySaveVoteRequest request =
                    PartySaveVoteRequestFixture.fixture(null, fromTime, toTime, mealTime);

            // when
            final Boolean result = request.isMealTimeAfter();

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("투표 시작 시간은 null이고, 투표 종료 시간이 식사 시간 보다 이전이면 true를 반환합니다.")
        void isBeforToTime() {
            // given
            final LocalTime mealTime = LocalTime.of(11, 0, 0);
            final LocalTime fromTime = null;
            final LocalTime toTime = LocalTime.of(10, 30, 0);

            final PartySaveVoteRequest request =
                    PartySaveVoteRequestFixture.fixture(null, fromTime, toTime, mealTime);

            // when
            final Boolean result = request.isMealTimeAfter();

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("투표 시작 시간과 투표 종료 시간이 식사 시간 보다 이전이면 true를 반환합니다.")
        void isBeforeTime() {
            // given
            final LocalTime mealTime = LocalTime.of(11, 0, 0);
            final LocalTime fromTime = LocalTime.of(10, 20, 0);
            final LocalTime toTime = LocalTime.of(10, 30, 0);

            final PartySaveVoteRequest request =
                    PartySaveVoteRequestFixture.fixture(null, fromTime, toTime, mealTime);

            // when
            final Boolean result = request.isMealTimeAfter();

            // then
            assertThat(result).isTrue();
        }
    }
}
