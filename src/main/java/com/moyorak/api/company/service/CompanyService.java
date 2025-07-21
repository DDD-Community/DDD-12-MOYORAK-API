package com.moyorak.api.company.service;

import com.moyorak.api.company.domain.Company;
import com.moyorak.api.company.domain.CompanySearch;
import com.moyorak.api.company.dto.CompanySaveRequest;
import com.moyorak.api.company.dto.CompanySearchListResponse;
import com.moyorak.api.company.dto.CompanySearchRequest;
import com.moyorak.api.company.repository.CompanyRepository;
import com.moyorak.api.company.repository.CompanySearchRepository;
import com.moyorak.config.exception.BusinessException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanySearchRepository companySearchRepository;

    @Transactional
    public void save(CompanySaveRequest saveRequest) {
        final Company company = saveRequest.toCompany();

        final boolean isSaved =
                companyRepository
                        .findByNameAndRoundedLongitudeAndRoundedLatitudeAndUseTrue(
                                company.getName(),
                                company.getRoundedLongitude(),
                                company.getRoundedLatitude())
                        .isPresent();

        if (isSaved) {
            throw new BusinessException("회사가 이미 등록되어 있습니다.");
        }
        final Company saved = companyRepository.save(company);
        companySearchRepository.save(CompanySearch.create(saved.getId(), saved.getName()));
    }

    @Transactional(readOnly = true)
    public CompanySearchListResponse search(final CompanySearchRequest request) {
        final List<CompanySearch> companySearches =
                companySearchRepository.findByConditions(request.companyId(), request.name());

        return CompanySearchListResponse.from(companySearches);
    }
}
