package com.moyorak.api.party.domain;

import org.springframework.test.util.ReflectionTestUtils;

public class VoteFixture {

    public static Vote fixture(
            final VoteType type, final VoteStatus status, final boolean use, final Long partyId) {
        Vote vote = new Vote();
        ReflectionTestUtils.setField(vote, "type", type);
        ReflectionTestUtils.setField(vote, "status", status);
        ReflectionTestUtils.setField(vote, "use", use);
        ReflectionTestUtils.setField(vote, "partyId", partyId);
        return vote;
    }

    public static Vote fixture(
            final Long id,
            final VoteType type,
            final VoteStatus status,
            final boolean use,
            final Long partyId) {
        Vote vote = new Vote();
        ReflectionTestUtils.setField(vote, "id", id);
        ReflectionTestUtils.setField(vote, "type", type);
        ReflectionTestUtils.setField(vote, "status", status);
        ReflectionTestUtils.setField(vote, "use", use);
        ReflectionTestUtils.setField(vote, "partyId", partyId);
        return vote;
    }
}
