package com.moyorak.api.party.service;

import com.moyorak.api.party.domain.Party;
import com.moyorak.api.party.domain.PartyRestaurants;
import com.moyorak.api.party.domain.VoteRestaurantCandidate;
import com.moyorak.api.party.dto.PartyRestaurantAppendRequest;
import com.moyorak.api.party.dto.PartyRestaurantProjection;
import com.moyorak.api.party.dto.PartySaveRestaurantsRequest;
import com.moyorak.api.party.repository.PartyRepository;
import com.moyorak.api.party.repository.PartyRestaurantRepository;
import com.moyorak.api.party.repository.VoteRepository;
import com.moyorak.api.team.domain.NotTeamUserException;
import com.moyorak.api.team.domain.TeamUser;
import com.moyorak.api.team.repository.TeamUserRepository;
import com.moyorak.config.exception.BusinessException;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PartyRestaurantService {
    private final PartyRestaurantRepository partyRestaurantRepository;
    private final TeamUserRepository teamUserRepository;
    private final PartyRepository partyRepository;
    private final VoteRepository voteRepository;

    @Transactional(readOnly = true)
    public List<PartyRestaurantProjection> findPartyRestaurantInfo(List<Long> partyIds) {
        return partyRestaurantRepository.findPartyRestaurantInfo(partyIds);
    }

    @Transactional
    public void append(
            final PartyRestaurantAppendRequest request,
            final Long teamId,
            final Long userId,
            final Long partyId) {
        // 파티의 팀과 유저의 팀을 비교
        final TeamUser teamUser =
                teamUserRepository
                        .findByUserIdAndTeamIdAndUse(userId, teamId, true)
                        .orElseThrow(NotTeamUserException::new);

        final Party party =
                partyRepository
                        .findByIdAndUseTrue(partyId)
                        .orElseThrow(() -> new BusinessException("파티가 존재하지 않습니다."));

        validateSameTeam(teamUser, party);

        // 투표가 존재하는지
        boolean isPresent =
                voteRepository
                        .findByIdAndPartyIdAndUseTrue(request.voteId(), party.getId())
                        .isPresent();

        if (!isPresent) {
            throw new BusinessException("투표가 존재하지 않습니다.");
        }

        // 식당 추가하기를 위한 검증
        final List<VoteRestaurantCandidate> candidates =
                partyRestaurantRepository.findAllByVoteIdAndUseTrueForUpdate(request.voteId());
        final PartyRestaurants partyRestaurants = PartyRestaurants.from(candidates);

        if (partyRestaurants.isFull()) {
            throw new BusinessException("후보 식당 추가는 5개가 최대입니다.");
        }

        if (partyRestaurants.contains(request.teamRestaurantId())) {
            throw new BusinessException("이미 식당이 등록되어 있습니다.");
        }

        partyRestaurantRepository.save(request.toVoteRestaurantCandidate());
    }

    private void validateSameTeam(final TeamUser teamUser, final Party party) {
        if (!Objects.equals(teamUser.getTeam().getId(), party.getTeamId())) {
            throw new BusinessException("해당 팀에 존재하지 않는 파티입니다.");
        }
    }

    /**
     * 투표 참여 식당을 지정합니다.
     *
     * @param voteId 투표 고유 ID
     * @param restaurants 식당 고유 ID
     */
    @Transactional
    public void registerRestaurants(
            final Long voteId, final PartySaveRestaurantsRequest restaurants) {
        final List<VoteRestaurantCandidate> candidates =
                restaurants.getIds().stream()
                        .map(it -> VoteRestaurantCandidate.create(it.getRestaurantId(), voteId))
                        .toList();

        partyRestaurantRepository.saveAll(candidates);
    }
}
