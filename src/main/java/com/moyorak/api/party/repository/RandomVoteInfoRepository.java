package com.moyorak.api.party.repository;

import com.moyorak.api.party.domain.RandomVoteInfo;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

public interface RandomVoteInfoRepository extends JpaRepository<RandomVoteInfo, Long> {

    @QueryHints(
            @QueryHint(
                    name = "org.hibernate.comment",
                    value =
                            "RandomVoteInfoRepository.findByVoteIdAndUseTrue : 투표 ID로 RandomVoteInfo를 조회합니다."))
    Optional<RandomVoteInfo> findByVoteIdAndUseTrue(Long voteId);

    @Modifying
    @Query(
            """
            UPDATE RandomVoteInfo rv
            SET rv.selectedCandidateId = :selectedCandidateId
            WHERE
                rv.id = :id
                AND rv.selectedCandidateId IS NULL
                AND rv.use = true
        """)
    @QueryHints(
            @QueryHint(
                    name = "org.hibernate.comment",
                    value =
                            "RandomVoteInfoRepository.updateSelectedCandidate : 랜덤으로 선택된 후보 ID를 업데이트 합니다."))
    int updateSelectedCandidate(
            @Param("id") Long id, @Param("selectedCandidateId") Long selectedCandidateId);

    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("SELECT r.selectedCandidateId FROM RandomVoteInfo r WHERE r.id = :id")
    @QueryHints(
            @QueryHint(
                    name = "org.hibernate.comment",
                    value =
                            "RandomVoteInfoRepository.findSelectedCandidateIdById : 공유락을 갖고 선택된 후보 ID를 조회합니다."))
    Long findSelectedCandidateIdById(@Param("id") Long id);
}
