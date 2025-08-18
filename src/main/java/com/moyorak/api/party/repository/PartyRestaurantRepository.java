package com.moyorak.api.party.repository;

import com.moyorak.api.party.domain.VoteRestaurantCandidate;
import com.moyorak.api.party.dto.PartyRestaurantProjection;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

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

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(
            """
            SELECT vrc
            FROM VoteRestaurantCandidate vrc
            WHERE vrc.voteId = :voteId AND vrc.use = true
        """)
    @QueryHints(
            @QueryHint(
                    name = "org.hibernate.comment",
                    value =
                            "PartyRestaurantRepository.findAllByVoteIdAndUseTrueForUpdate: 투표 ID로 후보 식당 리스트를 조회힙니다.(배타 락 사용)"))
    List<VoteRestaurantCandidate> findAllByVoteIdAndUseTrueForUpdate(@Param("voteId") Long voteId);

    @QueryHints(
            @QueryHint(
                    name = "org.hibernate.comment",
                    value = "PartyRestaurantRepository.findByIdAndUseTrue: ID로 후보 식당을 조회힙니다."))
    Optional<VoteRestaurantCandidate> findByIdAndUseTrue(Long candidateId);
}
