package com.moyorak.api.party.service;

import com.moyorak.api.party.domain.Party;
import com.moyorak.api.party.domain.VoteRecord;
import com.moyorak.api.party.domain.VoteRestaurantCandidate;
import com.moyorak.api.party.dto.VoteRequest;
import com.moyorak.api.team.domain.TeamUser;
import com.moyorak.api.team.domain.TeamUserNotFoundException;
import com.moyorak.api.team.service.TeamUserService;
import com.moyorak.config.exception.BusinessException;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VoteFacade {
    private final TeamUserService teamUserService;
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

        // 파티의 팀과 유저의 팀을 비교
        final TeamUser teamUser = teamUserService.getTeamUserByUserIdAndTeamId(userId, teamId);

        final Party party = partyService.getParty(partyId);

        if (!Objects.equals(teamUser.getTeam().getId(), party.getTeamId())) {
            throw new TeamUserNotFoundException();
        }

        // 파티 참가자가 존재하는지
        partyAttendeeService.getPartyAttendeeByUserIdAndPartyId(userId, partyId);

        // 투표가 존재하는지
        voteService.getVoteByIdAndPartyId(voteId, partyId);

        // 후보가 존재하는지
        final VoteRestaurantCandidate candidate =
                partyRestaurantService.getById(voteRequest.candidateId());
        if (!Objects.equals(candidate.getVoteId(), voteId)) {
            throw new BusinessException("해당 투표의 후보가 아닙니다.");
        }

        // 재투표시, 기존 데이터 사용 유무 불가 처리
        Optional<VoteRecord> voteRecord =
                voteRecordService.findByVoteIdAndUserIdAndUseTrue(userId, voteId);
        boolean isVoteRecordPresent = voteRecord.isPresent();
        if (isVoteRecordPresent) {
            VoteRecord existingRecord = voteRecord.get();
            if (Objects.equals(
                    existingRecord.getVoteRestaurantCandidateId(), voteRequest.candidateId())) {
                throw new BusinessException("이미 투표한 후보 입니다.");
            }
            existingRecord.toggleUse();
        }
        // 투표 기록 저장
        voteRecordService.save(VoteRecord.create(voteRequest.candidateId(), voteId, userId));
    }
}
