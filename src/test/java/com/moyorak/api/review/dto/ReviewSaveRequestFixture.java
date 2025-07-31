package com.moyorak.api.review.dto;

import java.util.List;

public class ReviewSaveRequestFixture {
    public static ReviewSaveRequest fixture(
            final Long userId,
            final Long servingTimeId,
            final Long waitingTimeId,
            final Integer score,
            final List<String> photoPaths,
            final String extraText) {
        return new ReviewSaveRequest(
                userId, servingTimeId, waitingTimeId, score, photoPaths, extraText);
    }
}
