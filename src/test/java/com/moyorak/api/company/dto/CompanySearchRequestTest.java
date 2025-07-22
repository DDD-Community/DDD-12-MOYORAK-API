package com.moyorak.api.company.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CompanySearchRequestTest {

    @Nested
    @DisplayName("입력 값이 최소 하나는 필수인 것을 검증할 때,")
    class hasAtLeastOneCondition {

        @Test
        @DisplayName("입력 값 모두 null이면 false를 반환한다.")
        void isNull() {
            // given
            final CompanySearchRequest request = new CompanySearchRequest(null, null);

            // when
            final boolean result = request.hasAtLeastOneCondition();

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("회사 고유 ID만 존재하여도 true를 반환한다.")
        void isIdNotNull() {
            // given
            final Long companyId = 1L;
            final CompanySearchRequest request = new CompanySearchRequest(companyId, null);

            // when
            final boolean result = request.hasAtLeastOneCondition();

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("회사 이름만 존재하여도 true를 반환한다.")
        void isIdNameNull() {
            // given
            final String name = "우가우가";
            final CompanySearchRequest request = new CompanySearchRequest(null, name);

            // when
            final boolean result = request.hasAtLeastOneCondition();

            // then
            assertThat(result).isTrue();
        }
    }
}
