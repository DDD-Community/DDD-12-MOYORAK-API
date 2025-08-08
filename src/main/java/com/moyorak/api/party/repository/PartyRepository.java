package com.moyorak.api.party.repository;

import com.moyorak.api.party.domain.Party;
import com.moyorak.api.party.dto.PartyGeneralInfoProjection;
import jakarta.persistence.QueryHint;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;

public interface PartyRepository extends CrudRepository<Party, Long> {
    List<Party> findAllByTeamIdAndUseIsTrue(Long teamId);

    @Query(
            """
        SELECT new com.moyorak.api.party.dto.PartyGeneralInfoProjection(
            p.id,
            COALESCE(s.startDate, r.randomDate),
            p.title,
            v.type,
            v.status,
            COUNT(DISTINCT pa.id)
        )
        FROM Party p
        LEFT JOIN Vote v
               ON v.partyId = p.id AND v.use = true
        LEFT JOIN SelectionVoteInfo s
               ON s.voteId = v.id AND v.type = com.moyorak.api.party.domain.VoteType.SELECT AND s.use = true
        LEFT JOIN RandomVoteInfo r
               ON r.voteId = v.id AND v.type = com.moyorak.api.party.domain.VoteType.RANDOM AND r.use = true
        LEFT JOIN PartyAttendee pa
               ON pa.partyId = p.id AND pa.use = true
        WHERE p.teamId = :teamId
          AND p.use = true
        GROUP BY
          p.id, p.title, v.type, v.status, COALESCE(s.startDate, r.randomDate)
        """)
    @QueryHints(
            @QueryHint(
                    name = "org.hibernate.comment",
                    value = "PartyRepository.findPartyGeneralInfos: 팀 ID 로 파티 일반 정보 조회"))
    List<PartyGeneralInfoProjection> findPartyGeneralInfos(Long teamId);
}
