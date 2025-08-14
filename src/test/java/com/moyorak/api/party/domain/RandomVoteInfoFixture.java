package com.moyorak.api.party.domain;

import java.time.LocalDateTime;
import org.springframework.test.util.ReflectionTestUtils;

public class RandomVoteInfoFixture {

    public static RandomVoteInfo fixture(final LocalDateTime randomDate) {
        RandomVoteInfo info = new RandomVoteInfo();
        ReflectionTestUtils.setField(info, "randomDate", randomDate);
        return info;
    }

    public static RandomVoteInfo fixture(
            final LocalDateTime randomDate,
            final LocalDateTime mealDate,
            final Long voteId,
            final boolean use,
            final Long selectedCandidateId) {
        RandomVoteInfo info = new RandomVoteInfo();
        ReflectionTestUtils.setField(info, "randomDate", randomDate);
        ReflectionTestUtils.setField(info, "mealDate", mealDate);
        ReflectionTestUtils.setField(info, "voteId", voteId);
        ReflectionTestUtils.setField(info, "use", use);
        ReflectionTestUtils.setField(info, "selectedCandidateId", selectedCandidateId);
        return info;
    }
}
