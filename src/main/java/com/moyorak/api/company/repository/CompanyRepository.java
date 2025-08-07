package com.moyorak.api.company.repository;

import com.moyorak.api.company.domain.Company;
import jakarta.persistence.QueryHint;
import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.QueryHints;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByNameAndRoundedLongitudeAndRoundedLatitudeAndUseTrue(
            String name, BigDecimal roundedLongitude, BigDecimal roundedLatitude);

    @QueryHints(
            @QueryHint(
                    name = "org.hibernate.comment",
                    value = "CompanyRepository.findByIdAndUseTrue : 회사 ID로 존재하는 회사를 조회합니다."))
    Optional<Company> findByIdAndUseTrue(Long id);
}
