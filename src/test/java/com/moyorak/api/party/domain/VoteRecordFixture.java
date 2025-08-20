package com.moyorak.api.party.domain;

import org.springframework.test.util.ReflectionTestUtils;

public class VoteRecordFixture {

    public static VoteRecord fixture(
            final Long id,
            final Long voteId,
            final Long voteRestaurantCandidateId,
            final Long attendeeId,
            final boolean use) {
        VoteRecord voteRecord = new VoteRecord();
        ReflectionTestUtils.setField(voteRecord, "id", id);
        ReflectionTestUtils.setField(voteRecord, "voteId", voteId);
        ReflectionTestUtils.setField(
                voteRecord, "voteRestaurantCandidateId", voteRestaurantCandidateId);
        ReflectionTestUtils.setField(voteRecord, "attendeeId", attendeeId);
        ReflectionTestUtils.setField(voteRecord, "use", use);
        return voteRecord;
    }
}
