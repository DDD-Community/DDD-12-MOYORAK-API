package com.moyorak.api.party.repository;

import com.moyorak.api.party.domain.VoteRestaurantCandidate;
import jakarta.persistence.QueryHint;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.QueryHints;

public interface VoteRestaurantCandidateRepository
        extends JpaRepository<VoteRestaurantCandidate, Long> {

    @QueryHints(
            @QueryHint(
                    name = "org.hibernate.comment",
                    value =
                            "VoteRestaurantCandidateRepository.findAllByVoteIdAndUseTrue : 투표 ID로 후보 식당 리스트를 조회합니다."))
    List<VoteRestaurantCandidate> findAllByVoteIdAndUseTrue(Long voteId);
}
