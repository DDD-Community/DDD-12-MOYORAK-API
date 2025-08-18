package com.moyorak.api.party.domain;

import java.util.List;
import java.util.Objects;

public record PartyRestaurants(List<VoteRestaurantCandidate> candidates) {

    private static final int MAX_CANDIDATES = 5;

    public static PartyRestaurants from(List<VoteRestaurantCandidate> candidates) {
        return new PartyRestaurants(candidates);
    }

    public boolean isFull() {
        return candidates.size() >= MAX_CANDIDATES;
    }

    public boolean contains(final Long teamRestaurantId) {
        for (VoteRestaurantCandidate candidate : candidates) {
            if (Objects.equals(candidate.getTeamRestaurantId(), teamRestaurantId)) {
                return true;
            }
        }
        return false;
    }
}
