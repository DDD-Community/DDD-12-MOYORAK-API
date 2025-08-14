package com.moyorak.api.party.dto;

import com.moyorak.api.party.domain.VoteRestaurantCandidate;
import java.util.List;

public record VoteDetail(VoteInfo voteInfo, List<RestaurantCandidate> RestaurantCandidates) {
    public static VoteDetail from(
            final VoteInfo voteInfo, final List<VoteRestaurantCandidate> candidates) {
        final List<RestaurantCandidate> RestaurantCandidates =
                candidates.stream().map(RestaurantCandidate::from).toList();
        return new VoteDetail(voteInfo, RestaurantCandidates);
    }

    public List<Long> getTeamRestaurantIds() {
        return RestaurantCandidates.stream().map(RestaurantCandidate::teamRestaurantId).toList();
    }

    public List<Long> getCandidateIds() {
        return RestaurantCandidates.stream().map(RestaurantCandidate::id).toList();
    }
}
