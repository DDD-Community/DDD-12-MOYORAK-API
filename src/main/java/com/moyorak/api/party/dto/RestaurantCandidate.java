package com.moyorak.api.party.dto;

import com.moyorak.api.party.domain.VoteRestaurantCandidate;

public record RestaurantCandidate(Long id, Long teamRestaurantId) {
    public static RestaurantCandidate from(final VoteRestaurantCandidate candidate) {
        return new RestaurantCandidate(candidate.getId(), candidate.getTeamRestaurantId());
    }
}
