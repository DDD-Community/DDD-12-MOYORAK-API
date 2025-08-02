package com.moyorak.api.review.domain;

import com.moyorak.api.image.ImageStore;
import com.moyorak.api.review.dto.ReviewPhotoPath;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReviewPhotoPaths {
    private final Map<Long, List<String>> reviewPhotoPathsMap;

    private ReviewPhotoPaths(final Map<Long, List<String>> reviewPhotoPathsMap) {
        this.reviewPhotoPathsMap = reviewPhotoPathsMap;
    }

    public static ReviewPhotoPaths create(
            final List<ReviewPhotoPath> reviewPhotoPaths, final ImageStore imageStore) {
        final Map<Long, List<String>> reviewPhotoPathsMap =
                reviewPhotoPaths.stream()
                        .collect(
                                Collectors.groupingBy(
                                        ReviewPhotoPath::reviewId,
                                        Collectors.mapping(
                                                path ->
                                                        imageStore.getUrlFromStringPath(
                                                                path.reviewPhotoPath()),
                                                Collectors.toList())));

        return new ReviewPhotoPaths(reviewPhotoPathsMap);
    }

    public List<String> getPhotoPaths(final Long reviewId) {
        return reviewPhotoPathsMap.getOrDefault(reviewId, List.of());
    }
}
