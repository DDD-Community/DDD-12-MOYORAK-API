package com.moyorak.api.review.domain;

import org.springframework.test.util.ReflectionTestUtils;

public class ReviewWaitingTimeFixture {

    public static ReviewWaitingTime fixture(
            final Long id, final String waitingTime, final boolean ues) {
        ReviewWaitingTime reviewWaitingTime = new ReviewWaitingTime();

        ReflectionTestUtils.setField(reviewWaitingTime, "id", id);
        ReflectionTestUtils.setField(reviewWaitingTime, "waitingTime", waitingTime);
        ReflectionTestUtils.setField(reviewWaitingTime, "use", ues);

        return reviewWaitingTime;
    }

    public static ReviewWaitingTime fixture(
            final Long id,
            final String waitingTime,
            final boolean ues,
            final Integer waitingTimeValue) {
        ReviewWaitingTime reviewWaitingTime = new ReviewWaitingTime();

        ReflectionTestUtils.setField(reviewWaitingTime, "id", id);
        ReflectionTestUtils.setField(reviewWaitingTime, "waitingTime", waitingTime);
        ReflectionTestUtils.setField(reviewWaitingTime, "use", ues);
        ReflectionTestUtils.setField(reviewWaitingTime, "waitingTimeValue", waitingTimeValue);

        return reviewWaitingTime;
    }
}
