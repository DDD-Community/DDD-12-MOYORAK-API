package com.moyorak.api.party.domain;

import java.time.LocalDateTime;
import org.springframework.test.util.ReflectionTestUtils;

public class SelectionVoteInfoFixture {

    public static SelectionVoteInfo fixture(
            final LocalDateTime startDate, final LocalDateTime expiredDate) {
        SelectionVoteInfo info = new SelectionVoteInfo();
        ReflectionTestUtils.setField(info, "startDate", startDate);
        ReflectionTestUtils.setField(info, "expiredDate", expiredDate);
        return info;
    }

    public static SelectionVoteInfo fixture(
            final LocalDateTime mealDAte,
            final LocalDateTime startDate,
            final LocalDateTime expiredDate) {
        SelectionVoteInfo info = new SelectionVoteInfo();
        ReflectionTestUtils.setField(info, "mealDate", mealDAte);
        ReflectionTestUtils.setField(info, "startDate", startDate);
        ReflectionTestUtils.setField(info, "expiredDate", expiredDate);
        return info;
    }
}
