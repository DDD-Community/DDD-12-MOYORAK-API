package com.moyorak.api.party.repository;

import com.moyorak.api.party.domain.SelectionVoteInfo;
import jakarta.persistence.QueryHint;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.QueryHints;

public interface SelectionVoteInfoRepository extends JpaRepository<SelectionVoteInfo, Long> {

    @QueryHints(
            @QueryHint(
                    name = "org.hibernate.comment",
                    value =
                            "SelectionVoteInfoRepository.findByVoteIdAndUseTrue : 투표 ID로 SelectionVoteInfo를 조회합니다."))
    Optional<SelectionVoteInfo> findByVoteIdAndUseTrue(Long voteId);
}
