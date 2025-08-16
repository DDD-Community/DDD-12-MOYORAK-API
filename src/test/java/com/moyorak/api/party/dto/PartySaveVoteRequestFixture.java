package com.moyorak.api.party.dto;

import com.moyorak.api.party.domain.VoteType;
import java.time.LocalTime;
import org.springframework.test.util.ReflectionTestUtils;

public class PartySaveVoteRequestFixture {

    public static PartySaveVoteRequest fixture(
            final VoteType voteType,
            final LocalTime fromTime,
            final LocalTime toTime,
            final LocalTime mealTime) {
        PartySaveVoteRequest request = new PartySaveVoteRequest();

        ReflectionTestUtils.setField(request, "voteType", voteType);
        ReflectionTestUtils.setField(request, "fromTime", fromTime);
        ReflectionTestUtils.setField(request, "toTime", toTime);
        ReflectionTestUtils.setField(request, "mealTime", mealTime);

        return request;
    }
}
