package com.moyorak.api.party.service;

import com.moyorak.api.party.domain.RandomVoteInfo;
import com.moyorak.api.party.domain.SelectionVoteInfo;
import com.moyorak.api.party.domain.Vote;
import com.moyorak.api.party.domain.VoteRestaurantCandidate;
import com.moyorak.api.party.dto.PartySaveRequest;
import com.moyorak.api.party.dto.VoteDetail;
import com.moyorak.api.party.dto.VoteInfo;
import com.moyorak.api.party.repository.PartyRestaurantRepository;
import com.moyorak.api.party.repository.RandomVoteInfoRepository;
import com.moyorak.api.party.repository.SelectionVoteInfoRepository;
import com.moyorak.api.party.repository.VoteRepository;
import com.moyorak.config.exception.BusinessException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class VoteService {

    private final VoteRepository voteRepository;
    private final SelectionVoteInfoRepository selectionVoteInfoRepository;
    private final RandomVoteInfoRepository randomVoteInfoRepository;
    private final PartyRestaurantRepository partyRestaurantRepository;

    @Transactional(readOnly = true)
    public Vote getVoteByIdAndPartyId(final Long voteId, final Long partyId) {
        return voteRepository
                .findByIdAndPartyIdAndUseTrue(voteId, partyId)
                .orElseThrow(() -> new BusinessException("투표가 존재하지 않습니다."));
    }

    @Transactional
    public VoteDetail getVoteDetail(final Long partyId, final LocalDateTime now) {
        // 투표 정보 가져오기
        final Vote vote =
                voteRepository
                        .findByPartyIdAndUseTrue(partyId)
                        .orElseThrow(() -> new BusinessException("투표가 존재하지 않습니다."));

        // 후보 식당 가져오기
        final List<VoteRestaurantCandidate> candidates =
                partyRestaurantRepository.findAllByVoteIdAndUseTrue(vote.getId());

        // 선택 투표인 경우
        if (vote.isSelectVote()) {
            final SelectionVoteInfo selectionVoteInfo =
                    selectionVoteInfoRepository
                            .findByVoteIdAndUseTrue(vote.getId())
                            .orElseThrow(() -> new BusinessException("선택 투표 정보가 존재하지 않습니다."));

            vote.changeStatusByNowForSelectionVote(now, selectionVoteInfo);

            final VoteInfo voteInfo = VoteInfo.from(vote, selectionVoteInfo);

            return VoteDetail.from(voteInfo, candidates);
        }

        // 랜덤 투표인 경우
        final RandomVoteInfo randomVoteInfo =
                randomVoteInfoRepository
                        .findByVoteIdAndUseTrue(vote.getId())
                        .orElseThrow(() -> new BusinessException("랜덤 투표 정보가 존재하지 않습니다."));

        vote.changeStatusByNowForRandomVote(now, randomVoteInfo);
        selectRandomCandidateIfDone(vote, randomVoteInfo, candidates);

        final VoteInfo voteInfo = VoteInfo.from(vote, randomVoteInfo);

        return VoteDetail.from(voteInfo, candidates);
    }

    private void selectRandomCandidateIfDone(
            final Vote vote,
            final RandomVoteInfo randomVoteInfo,
            final List<VoteRestaurantCandidate> candidates) {

        if (!vote.isDone() || randomVoteInfo.hasSelectedCandidate()) {
            return;
        }

        if (candidates.isEmpty()) {
            return;
        }

        final int index = ThreadLocalRandom.current().nextInt(candidates.size());
        final Long pickId = candidates.get(index).getId();

        final int updated =
                randomVoteInfoRepository.updateSelectedCandidate(randomVoteInfo.getId(), pickId);

        if (updated == 1) {
            randomVoteInfo.confirmRandomCandidate(pickId);
            return;
        }

        // 다른 트랜잭션이 이미 변경한 경우 ID를 직접 DB에 Share lock을 통해 최신 커밋 조회
        final Long selectedCandidateId =
                randomVoteInfoRepository.findSelectedCandidateIdById(randomVoteInfo.getId());
        randomVoteInfo.confirmRandomCandidate(selectedCandidateId);
    }

    @Transactional
    public void updateVoteStatus(final Long partyId, final LocalDateTime now) {
        final Vote vote =
                voteRepository
                        .findByPartyIdAndUseTrue(partyId)
                        .orElseThrow(() -> new BusinessException("투표가 존재하지 않습니다."));

        if (vote.isSelectVote()) {
            final SelectionVoteInfo selectionVoteInfo =
                    selectionVoteInfoRepository
                            .findByVoteIdAndUseTrue(vote.getId())
                            .orElseThrow(() -> new BusinessException("선택 투표 정보가 존재하지 않습니다."));
            vote.changeStatusByNowForSelectionVote(now, selectionVoteInfo);

        } else {
            final RandomVoteInfo randomVoteInfo =
                    randomVoteInfoRepository
                            .findByVoteIdAndUseTrue(vote.getId())
                            .orElseThrow(() -> new BusinessException("랜덤 투표 정보가 존재하지 않습니다."));
            vote.changeStatusByNowForRandomVote(now, randomVoteInfo);
        }
    }

    /**
     * 투표를 생성합니다.
     *
     * @param partyId 파티 고유 ID
     * @param request 파티 생성 요청 DTO
     * @return 투표 고유 ID
     */
    @Transactional
    public Long register(final Long partyId, final PartySaveRequest request) {
        final Vote vote = voteRepository.save(Vote.create(partyId, request.getVoteType()));

        final LocalDate now = LocalDate.now();

        if (request.isVoteTypeSelect()) {
            selectionVoteInfoRepository.save(
                    SelectionVoteInfo.generate(
                            vote.getId(),
                            now,
                            request.getVoteStartTime(),
                            request.getVoteEndTime(),
                            request.getPartyMealTime()));

            return vote.getId();
        }

        randomVoteInfoRepository.save(
                RandomVoteInfo.generate(vote.getId(), now, request.getVoteEndTime()));

        return vote.getId();
    }
}
