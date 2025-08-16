package com.moyorak.api.party.domain;

import org.springframework.test.util.ReflectionTestUtils;

public class VoteRestaurantCandidateFixture {

    public static VoteRestaurantCandidate fixture(
            Long id, Long voteId, Long teamRestaurantId, boolean use) {
        VoteRestaurantCandidate candidate = new VoteRestaurantCandidate();
        ReflectionTestUtils.setField(candidate, "id", id);
        ReflectionTestUtils.setField(candidate, "voteId", voteId);
        ReflectionTestUtils.setField(candidate, "teamRestaurantId", teamRestaurantId);
        ReflectionTestUtils.setField(candidate, "use", use);
        return candidate;
    }
}
