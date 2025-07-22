package com.moyorak.api.company.repository;

import com.moyorak.api.company.domain.CompanySearch;
import jakarta.persistence.QueryHint;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

public interface CompanySearchRepository extends JpaRepository<CompanySearch, Long> {
    @Query(
            value =
                    """
    SELECT c
    FROM CompanySearch c
    WHERE (:companyId IS NULL OR c.companyId = :companyId)
    AND (:name IS NULL OR c.name LIKE CONCAT('%', :name, '%'))
    AND c.use = true
    ORDER BY c.id ASC
    LIMIT 5
""")
    @QueryHints(
            @QueryHint(
                    name = "org.hibernate.comment",
                    value = "CompanySearchRepository.findByConditions : 회사를 검색합니다."))
    List<CompanySearch> findByConditions(
            @Param("companyId") Long companyId, @Param("name") String name);
}
