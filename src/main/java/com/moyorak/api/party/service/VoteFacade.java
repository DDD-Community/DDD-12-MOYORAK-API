package com.moyorak.api.party.service;

import com.moyorak.api.party.domain.PartyAttendee;
import com.moyorak.api.party.domain.VoteRestaurantCandidate;
import com.moyorak.api.party.dto.VoteRequest;
import com.moyorak.config.exception.BusinessException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VoteFacade {
    private final PartyService partyService;
    private final VoteService voteService;
    private final PartyAttendeeService partyAttendeeService;
    private final PartyRestaurantService partyRestaurantService;
    private final VoteRecordService voteRecordService;

    @Transactional
    public void vote(
            final Long teamId,
            final Long partyId,
            final Long voteId,
            final Long userId,
            final VoteRequest voteRequest) {

        // 팀에 존재하는 파티인지
        partyService.getPartyInfo(partyId, teamId);

        // 파티 참가자가 존재하는지
        PartyAttendee partyAttendee =
                partyAttendeeService.getPartyAttendeeByUserIdAndPartyId(userId, partyId);

        // 투표가 존재하는지
        voteService.getVoteByIdAndPartyId(voteId, partyId);

        // 후보가 존재하는지
        final VoteRestaurantCandidate candidate =
                partyRestaurantService.getById(voteRequest.candidateId());
        if (!Objects.equals(candidate.getVoteId(), voteId)) {
            throw new BusinessException("해당 투표의 후보가 아닙니다.");
        }

        // 투표 기록 저장
        voteRecordService.vote(partyAttendee.getId(), voteId, voteRequest.candidateId());
    }
}
