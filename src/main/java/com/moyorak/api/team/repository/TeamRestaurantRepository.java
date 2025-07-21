package com.moyorak.api.team.repository;

import com.moyorak.api.team.domain.TeamRestaurant;
import com.moyorak.api.team.dto.TeamRestaurantLocation;
import com.moyorak.api.team.dto.TeamRestaurantSummary;
import jakarta.persistence.QueryHint;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface TeamRestaurantRepository extends CrudRepository<TeamRestaurant, Long> {
    @EntityGraph(attributePaths = "restaurant")
    Optional<TeamRestaurant> findByTeamIdAndIdAndUse(Long teamId, Long id, boolean use);

    Optional<TeamRestaurant> findByTeamIdAndRestaurantIdAndUse(
            Long teamId, Long restaurantId, boolean use);

    @QueryHints(
            @QueryHint(
                    name = "org.hibernate.comment",
                    value =
                            "TeamRestaurantRepository.findByIdInAndUse : 팀 식당 ID 리스트로 팀 식당 요약 정보을 조회합니다."))
    @Query(
            """
SELECT new com.moyorak.api.team.dto.TeamRestaurantSummary(tr.id, r.name, r.category, tr.averageReviewScore, tr.reviewCount)
FROM TeamRestaurant tr
JOIN tr.restaurant r
WHERE tr.id IN :ids AND tr.use = :use
""")
    List<TeamRestaurantSummary> findByIdInAndUse(
            @Param("ids") List<Long> ids, @Param("use") boolean use);

    @QueryHints(
            @QueryHint(
                    name = "org.hibernate.comment",
                    value =
                            "TeamRestaurantRepository.findLocationsByTeamIdAndUse : 팀 ID로 팀 식당 모든 좌표값과 식당 이름을 조회합니다."))
    @Query(
            """
    SELECT new com.moyorak.api.team.dto.TeamRestaurantLocation(tr.id, r.name, r.longitude, r.latitude)
    FROM TeamRestaurant tr
    JOIN Restaurant r ON tr.restaurant.id = r.id
    WHERE tr.teamId = :teamId AND tr.use = :use
    """)
    List<TeamRestaurantLocation> findLocationsByTeamIdAndUse(
            @Param("teamId") Long teamId, @Param("use") boolean use);

    Page<TeamRestaurant> findAllByTeamId(Long teamId, Pageable pageable);
}
