package com.moyorak.api.party.repository;

import com.moyorak.api.party.domain.Vote;
import jakarta.persistence.QueryHint;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.QueryHints;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    @QueryHints(
            @QueryHint(
                    name = "org.hibernate.comment",
                    value = "VoteRepository.findByPartyIdAndUseTrue : 파티ID로 투표를 조회합니다."))
    Optional<Vote> findByPartyIdAndUseTrue(Long partyId);
}
