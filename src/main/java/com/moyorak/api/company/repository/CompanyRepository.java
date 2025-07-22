package com.moyorak.api.company.repository;

import com.moyorak.api.company.domain.Company;
import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByNameAndRoundedLongitudeAndRoundedLatitudeAndUseTrue(
            String name, BigDecimal roundedLongitude, BigDecimal roundedLatitude);
}
