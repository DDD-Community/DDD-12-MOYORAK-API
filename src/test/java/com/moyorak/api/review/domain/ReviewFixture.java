package com.moyorak.api.review.domain;

import org.springframework.test.util.ReflectionTestUtils;

public class ReviewFixture {

    public static Review fixture(
            final Long id,
            final Integer score,
            final Integer servingTime,
            final Integer waitingTime,
            final String extraText,
            final Long userId,
            final Long teamRestaurantId) {
        Review review = new Review();
        ReflectionTestUtils.setField(review, "id", id);
        ReflectionTestUtils.setField(review, "score", score);
        ReflectionTestUtils.setField(review, "servingTime", servingTime);
        ReflectionTestUtils.setField(review, "waitingTime", waitingTime);
        ReflectionTestUtils.setField(review, "extraText", extraText);
        ReflectionTestUtils.setField(review, "userId", userId);
        ReflectionTestUtils.setField(review, "teamRestaurantId", teamRestaurantId);
        return review;
    }
}
