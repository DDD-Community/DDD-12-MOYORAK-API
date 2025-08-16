package com.moyorak.api.party.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class VoteTypeTest {

    @Nested
    @DisplayName("입력 값을 받아 VoteType Enum 값 반환할 때,")
    class from {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "NOT_FOUND"})
        @DisplayName("null이거나 유효하지 않은 값인 경우, null로 반환합니다.")
        void isNotFoundOrNull(final String input) {
            // when
            final VoteType result = VoteType.from(input);

            // then
            assertThat(result).isNull();
        }

        @ParameterizedTest
        @EnumSource(value = VoteType.class)
        @DisplayName("유효한 값이 반환되는 경우, 성공적으로 VoteType Enum을 반환합니다.")
        void isSuccess(final VoteType voteType) {
            // given
            final String input = voteType.name();

            // when
            final VoteType result = VoteType.from(input);

            // then
            assertThat(result).isEqualTo(voteType);
        }
    }

    @Nested
    @DisplayName("투표 방식이 선택(SELECT)인지 확인할 때,")
    class isSelect {

        @Test
        @DisplayName("SELECT면 true를 반환합니다.")
        void isTrue() {
            // given
            final VoteType input = VoteType.SELECT;

            // when
            final Boolean result = input.isSelect();

            // then
            assertThat(result).isTrue();
        }

        @ParameterizedTest
        @EnumSource(value = VoteType.class, names = "SELECT", mode = EnumSource.Mode.EXCLUDE)
        @DisplayName("RANDOM이면 false를 반환합니다.")
        void isFalse(final VoteType input) {
            // when
            final Boolean result = input.isSelect();

            // then
            assertThat(result).isFalse();
        }
    }
}
