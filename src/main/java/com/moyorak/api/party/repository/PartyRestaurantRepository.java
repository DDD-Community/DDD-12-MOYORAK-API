package com.moyorak.api.party.repository;

import com.moyorak.api.party.domain.VoteRestaurantCandidate;
import com.moyorak.api.party.dto.PartyRestaurantProjection;
import jakarta.persistence.QueryHint;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;

public interface PartyRestaurantRepository extends CrudRepository<VoteRestaurantCandidate, Long> {

    @Query(
            """
    SELECT new com.moyorak.api.party.dto.PartyRestaurantProjection(
        v.partyId,
            r.name,
        r.category,
        t.averageReviewScore,
        t.reviewCount
    )
    FROM Vote v
    JOIN VoteRestaurantCandidate vc ON vc.voteId = v.id AND vc.use = true
    JOIN TeamRestaurant t           ON t.id = vc.teamRestaurantId AND t.use = true
    JOIN t.restaurant r             WITH r.use = true
    WHERE v.use = true
      AND v.partyId IN (:partyIds)
    """)
    @QueryHints(
            @QueryHint(
                    name = "org.hibernate.comment",
                    value =
                            "PartyRestaurantRepository.findPartyRestaurantInfo: 파티 ID들 로 파티 투표 후보 식당들 정보 조회"))
    List<PartyRestaurantProjection> findPartyRestaurantInfo(List<Long> partyIds);

    @QueryHints(
            @QueryHint(
                    name = "org.hibernate.comment",
                    value =
                            "VoteRestaurantCandidateRepository.findAllByVoteIdAndUseTrue : 투표 ID로 후보 식당 리스트를 조회합니다."))
    List<VoteRestaurantCandidate> findAllByVoteIdAndUseTrue(Long voteId);
}
