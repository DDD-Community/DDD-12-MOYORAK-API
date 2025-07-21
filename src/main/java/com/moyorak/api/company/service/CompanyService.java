package com.moyorak.api.company.service;

import com.moyorak.api.company.domain.Company;
import com.moyorak.api.company.domain.CompanySearch;
import com.moyorak.api.company.dto.CompanySaveRequest;
import com.moyorak.api.company.repository.CompanyRepository;
import com.moyorak.api.company.repository.CompanySearchRepository;
import com.moyorak.config.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanySearchRepository companySearchRepository;

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
}
