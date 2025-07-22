package com.moyorak.api.team.repository;

import com.moyorak.api.team.domain.TeamRestaurantSearch;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRestaurantSearchRepository extends JpaRepository<TeamRestaurantSearch, Long> {
    Optional<TeamRestaurantSearch> findByTeamIdAndTeamRestaurantId(
            Long teamId, Long teamRestaurantId);
}
