package com.moyorak.api.team.repository;

import com.moyorak.api.company.domain.Company;
import com.moyorak.api.team.domain.Team;
import jakarta.persistence.QueryHint;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface TeamRepository extends CrudRepository<Team, Long> {

    boolean existsByCompanyAndName(Company company, String name);

    @Query(
            """
            SELECT t
            FROM Team t
            JOIN FETCH t.company
            WHERE t.id = :teamId
            and t.use = true
            and t.company.use = true
        """)
    @QueryHints(
            @QueryHint(
                    name = "org.hibernate.comment",
                    value = "TeamRepository.findByTeamId : 팀 고유 ID로 Team 정보를 조회합니다."))
    Optional<Team> findByTeamId(@Param("teamId") Long teamId, @Param("userId") Long userId);
}
