package com.moyorak.api.review.domain;

import org.springframework.test.util.ReflectionTestUtils;

public class ReviewPhotoFixture {
    public static ReviewPhoto fixture(
            final Long id, final String path, final boolean use, final Long reviewId) {
        ReviewPhoto reviewPhoto = new ReviewPhoto();
        ReflectionTestUtils.setField(reviewPhoto, "id", id);
        ReflectionTestUtils.setField(reviewPhoto, "path", path);
        ReflectionTestUtils.setField(reviewPhoto, "use", use);
        ReflectionTestUtils.setField(reviewPhoto, "reviewId", reviewId);

        return reviewPhoto;
    }
}
