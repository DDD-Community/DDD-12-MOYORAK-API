package com.moyorak.api.review.domain;

import org.springframework.test.util.ReflectionTestUtils;

public class ReviewServingTimeFixture {

    public static ReviewServingTime fixture(
            final Long id, final String servingTIme, final boolean ues) {
        ReviewServingTime reviewServingTime = new ReviewServingTime();

        ReflectionTestUtils.setField(reviewServingTime, "id", id);
        ReflectionTestUtils.setField(reviewServingTime, "servingTime", servingTIme);
        ReflectionTestUtils.setField(reviewServingTime, "use", ues);

        return reviewServingTime;
    }
}
