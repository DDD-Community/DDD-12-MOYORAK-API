package com.moyorak.api.team.repository;

import com.moyorak.api.team.domain.TeamSearch;
import jakarta.persistence.QueryHint;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface TeamSearchRepository extends CrudRepository<TeamSearch, Long> {

    @Query(
            value =
                    """
            SELECT t
            FROM TeamSearch t
            WHERE (:companyId IS NULL OR t.companyId = :companyId)
            AND (:teamId IS NULL OR t.id = :teamId)
            AND (:name IS NULL OR t.name LIKE CONCAT('%', :name, '%'))
            AND t.use = true
            ORDER BY t.id ASC
            LIMIT 5
        """)
    @QueryHints(
            @QueryHint(
                    name = "org.hibernate.comment",
                    value = "TeamSearchRepository.findByConditions : 팀 정보를 검색합니다."))
    List<TeamSearch> findByConditions(
            @Param("companyId") Long companyId,
            @Param("teamId") Long teamId,
            @Param("name") String name);
}
