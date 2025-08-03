package com.moyorak.api.review.dto;

import java.util.List;

public class ReviewUpdateRequestFixture {
    public static ReviewUpdateRequest fixture(
            final Long servingTimeId,
            final Long waitingTimeId,
            final Integer score,
            final List<String> photoPaths,
            final String extraText) {
        return new ReviewUpdateRequest(servingTimeId, waitingTimeId, score, photoPaths, extraText);
    }
}
