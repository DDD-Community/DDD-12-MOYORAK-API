package com.moyorak.api.party.repository;

import com.moyorak.api.party.domain.PartyAttendee;
import com.moyorak.api.party.dto.PartyAttendeeWithUserProfile;
import jakarta.persistence.QueryHint;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;

public interface PartyAttendeeRepository extends CrudRepository<PartyAttendee, Long> {
    @Query(
            """
SELECT new com.moyorak.api.party.dto.PartyAttendeeWithUserProfile(
      p.partyId,
      p.userId,
      u.profileImage
)
FROM PartyAttendee p
JOIN User u ON p.userId = u.id AND u.use = true
WHERE p.use = true
  AND p.partyId IN (:partyIds)
""")
    @QueryHints(
            @QueryHint(
                    name = "org.hibernate.comment",
                    value =
                            "PartyAttendeeRepository.findPartyAttendeeWithUser: 파티 ID들 로 파티 참가자 정보 조회"))
    List<PartyAttendeeWithUserProfile> findPartyAttendeeWithUser(List<Long> partyIds);

    @QueryHints(
            @QueryHint(
                    name = "org.hibernate.comment",
                    value =
                            "PartyAttendeeRepository.findByPartyIdAndUserIdAndUseTrue: 파티 ID와 유저 ID로 참석자 조회"))
    Optional<PartyAttendee> findByPartyIdAndUserIdAndUseTrue(Long partyId, Long userId);
}
