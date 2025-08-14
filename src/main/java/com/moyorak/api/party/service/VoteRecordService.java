package com.moyorak.api.party.service;

import com.moyorak.api.party.dto.Voter;
import com.moyorak.api.party.repository.VoteRecordRepository;
import java.util.List;
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
}
