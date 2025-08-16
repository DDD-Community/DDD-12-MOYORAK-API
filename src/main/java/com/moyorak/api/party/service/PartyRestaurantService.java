package com.moyorak.api.party.service;

import com.moyorak.api.party.domain.VoteRestaurantCandidate;
import com.moyorak.api.party.dto.PartyRestaurantProjection;
import com.moyorak.api.party.dto.PartySaveRestaurantsRequest;
import com.moyorak.api.party.repository.PartyRestaurantRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PartyRestaurantService {
    private final PartyRestaurantRepository partyRestaurantRepository;

    @Transactional(readOnly = true)
    public List<PartyRestaurantProjection> findPartyRestaurantInfo(List<Long> partyIds) {
        return partyRestaurantRepository.findPartyRestaurantInfo(partyIds);
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
