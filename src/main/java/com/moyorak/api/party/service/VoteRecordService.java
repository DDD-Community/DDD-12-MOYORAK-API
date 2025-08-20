package com.moyorak.api.party.service;

import com.moyorak.api.party.domain.VoteRecord;
import com.moyorak.api.party.dto.Voter;
import com.moyorak.api.party.repository.VoteRecordRepository;
import com.moyorak.config.exception.BusinessException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VoteRecordService {

    private final VoteRecordRepository voteRecordRepository;

    @Transactional(readOnly = true)
    public List<Voter> getVoters(final List<Long> candidateIds) {
        return voteRecordRepository.findAllByVoteRestaurantCandidateIdIn(candidateIds);
    }

    @Transactional(readOnly = true)
    public Optional<VoteRecord> findByVoteIdAndUserIdAndUseTrue(
            final Long userId, final Long voteId) {
        return voteRecordRepository.findByVoteIdAndAttendeeIdAndUseTrue(voteId, userId);
    }

    @Transactional
    public void vote(final Long userId, final Long voteId, final Long candidateId) {
        Optional<VoteRecord> voteRecord = findByVoteIdAndUserIdAndUseTrue(userId, voteId);
        boolean isVoteRecordPresent = voteRecord.isPresent();
        if (isVoteRecordPresent) {
            VoteRecord existingRecord = voteRecord.get();
            if (existingRecord.isSameCandidate(candidateId)) {
                throw new BusinessException("이미 투표한 후보 입니다.");
            }
            existingRecord.toggleUse();
        }
        voteRecordRepository.save(VoteRecord.create(candidateId, voteId, userId));
    }
}
