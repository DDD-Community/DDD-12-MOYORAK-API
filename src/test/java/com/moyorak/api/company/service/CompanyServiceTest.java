package com.moyorak.api.company.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.moyorak.api.company.domain.Company;
import com.moyorak.api.company.domain.CompanyFixture;
import com.moyorak.api.company.domain.CompanySearch;
import com.moyorak.api.company.domain.CompanySearchFixture;
import com.moyorak.api.company.dto.CompanySaveRequest;
import com.moyorak.api.company.dto.CompanySearchListResponse;
import com.moyorak.api.company.dto.CompanySearchRequest;
import com.moyorak.api.company.repository.CompanyRepository;
import com.moyorak.api.company.repository.CompanySearchRepository;
import com.moyorak.config.exception.BusinessException;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @InjectMocks private CompanyService companyService;

    @Mock private CompanyRepository companyRepository;

    @Mock private CompanySearchRepository companySearchRepository;

    @Nested
    @DisplayName("회사 등록 시")
    class Save {

        private CompanySaveRequest saveRequest;

        @BeforeEach
        void setUp() {
            saveRequest =
                    new CompanySaveRequest(
                            "우가우가 차차차", "우가우가시 차차차동 24번길", "우가우가동 차차차호", 127.043616, 37.503095);
        }

        @Test
        @DisplayName("저장을 성공합니다.")
        void success() {
            // given
            final Company company = saveRequest.toCompany();
            given(
                            companyRepository
                                    .findByNameAndRoundedLongitudeAndRoundedLatitudeAndUseTrue(
                                            company.getName(),
                                            company.getRoundedLongitude(),
                                            company.getRoundedLatitude()))
                    .willReturn(Optional.empty());

            // 식당 저장 시 ID가 포함된 객체 반환되도록 설정
            final Company saved = CompanyFixture.fixture(1L, "우가우가회사", 127.0, 37.5, true);

            given(companyRepository.save(any(Company.class))).willReturn(saved);

            // when
            companyService.save(saveRequest);

            // then
            then(companyRepository).should().save(any(Company.class));
            then(companySearchRepository).should().save(any(CompanySearch.class));
        }

        @Test
        @DisplayName("이미 등록된 회사가 있습니다.")
        void fail() {
            // given
            final Company company = saveRequest.toCompany();
            final Company existingCompany = CompanyFixture.fixture(1L, "우가우가회사", 127.0, 37.5, true);

            given(
                            companyRepository
                                    .findByNameAndRoundedLongitudeAndRoundedLatitudeAndUseTrue(
                                            company.getName(),
                                            company.getRoundedLongitude(),
                                            company.getRoundedLatitude()))
                    .willReturn(Optional.of(existingCompany));

            // when & then
            assertThatThrownBy(() -> companyService.save(saveRequest))
                    .isInstanceOf(BusinessException.class);
        }
    }

    @Nested
    @DisplayName("회사 검색 시")
    class Search {
        @Test
        @DisplayName("회사 검색 조건에 맞는 결과를 반환한다")
        void searchCompaniesByCondition_success() {
            // given
            final Long companySearchId = 1L;
            final Long companyId = 1L;
            final String name = "우가우가";
            final double longitude = 12.0;
            final double latitude = 37.5;

            final CompanySearchRequest request = new CompanySearchRequest(companyId, name);

            final CompanySearch companySearch =
                    CompanySearchFixture.fixture(
                            companySearchId, companyId, name, longitude, latitude, true); // 생성자 예시
            final List<CompanySearch> list = List.of(companySearch);

            given(companySearchRepository.findByConditions(companyId, name)).willReturn(list);

            // when
            final CompanySearchListResponse response = companyService.search(request);

            // then
            SoftAssertions.assertSoftly(
                    it -> {
                        it.assertThat(response).isNotNull();
                        it.assertThat(response.searchResponses()).hasSize(1);
                        it.assertThat(response.searchResponses().get(0).companyId())
                                .isEqualTo(companyId);
                        ;
                        it.assertThat(response.searchResponses().get(0).name()).isEqualTo(name);
                    });

            ;
        }
    }
}
