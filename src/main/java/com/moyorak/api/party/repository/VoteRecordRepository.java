package com.moyorak.api.party.repository;

import com.moyorak.api.party.domain.VoteRecord;
import com.moyorak.api.party.dto.Voter;
import jakarta.persistence.QueryHint;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

public interface VoteRecordRepository extends JpaRepository<VoteRecord, Long> {

    @Query(
            """
        SELECT new com.moyorak.api.party.dto.Voter(vr.voteRestaurantCandidateId, u.id, u.name, u.profileImage)
        FROM VoteRecord vr
        LEFT JOIN PartyAttendee pa ON pa.id = vr.attendeeId AND pa.use = true
        LEFT JOIN User u on u.id = pa.userId AND u.use = true
        WHERE
            vr.voteRestaurantCandidateId IN (:voteRestaurantCandidateIds)
            AND vr.use = true
    """)
    @QueryHints(
            @QueryHint(
                    name = "org.hibernate.comment",
                    value =
                            "VoteRecordRepository.findAllByVoteRestaurantCandidateIdIn : 후보 식당 ID 리스트로 투표자 리스트를 조회합니다."))
    List<Voter> findAllByVoteRestaurantCandidateIdIn(
            @Param("voteRestaurantCandidateIds") List<Long> voteRestaurantCandidateIds);

    @QueryHints(
            @QueryHint(
                    name = "org.hibernate.comment",
                    value =
                            "VoteRecordRepository.findByVoteIdAndAttendeeIdAndUseTrue : 투표 ID와 참가자 ID 로 투표 기록을 조회합니다."))
    Optional<VoteRecord> findByVoteIdAndAttendeeIdAndUseTrue(Long voteId, Long attendeeId);
}
