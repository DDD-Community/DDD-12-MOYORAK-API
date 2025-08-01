package com.moyorak.api.team.repository;

import com.moyorak.api.team.domain.TeamRestaurantSearch;
import jakarta.persistence.QueryHint;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

public interface TeamRestaurantSearchRepository extends JpaRepository<TeamRestaurantSearch, Long> {
    Optional<TeamRestaurantSearch> findByTeamIdAndTeamRestaurantId(
            Long teamId, Long teamRestaurantId);

    @Modifying
    @Query(
            """
            UPDATE TeamRestaurantSearch trs
            SET trs.averageReviewScore = :averageReviewScore
            WHERE trs.teamRestaurantId = :teamRestaurantId
""")
    @QueryHints(
            @QueryHint(
                    name = "org.hibernate.comment",
                    value =
                            "TeamRestaurantSearchRepository.updateAverageReviewScore : 팀 식당 검색의 평균 리뷰 점수를 업데이트 합니다. "))
    void updateAverageReviewScore(
            @Param("teamRestaurantId") Long teamRestaurantId,
            @Param("averageReviewScore") double averageReviewScore);
}
