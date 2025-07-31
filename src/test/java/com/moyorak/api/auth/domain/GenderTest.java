package com.moyorak.api.auth.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class GenderTest {

    @Nested
    @DisplayName("코드로 입력을 받아 Gender로 반환할 때,")
    class from {

        @Test
        @DisplayName("여성을 의미하는 FEMALE를 입력하면, Gender의 FEMALE로 반환됩니다.")
        void femaleSuccess() {
            // given
            final String input = "FEMALE";

            final Gender expectedResult = Gender.FEMALE;

            // when
            final Gender result = Gender.from(input);

            // then
            assertThat(result).isEqualTo(expectedResult);
        }

        @Test
        @DisplayName("남성을 의미하는 MALE를 입력하면, Gender의 MALE로 반환합니다.")
        void maleSuccess() {
            // given
            final String input = "MALE";

            final Gender expectedResult = Gender.MALE;

            // when
            final Gender result = Gender.from(input);

            // then
            assertThat(result).isEqualTo(expectedResult);
        }

        @Test
        @DisplayName("탈퇴를 의미하는 UNREGISTER를 입력하면, Gender의 UNREGISTER로 반한합니다.")
        void unregisterSuccess() {
            // given
            final String input = "UNREGISTER";

            final Gender expectedResult = Gender.UNREGISTER;

            // when
            final Gender result = Gender.from(input);

            // then
            assertThat(result).isEqualTo(expectedResult);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = " ")
        @DisplayName("유효하지 않은 값이 입력되면, null 값을 반환합니다.")
        void isNull(final String input) {
            // when
            final Gender result = Gender.from(input);

            // then
            assertThat(result).isNull();
        }
    }
}
