package com.moyorak.api.review.domain;

import com.moyorak.api.auth.dto.ReviewWithUserAndTeamRestaurantProjection;
import com.moyorak.api.review.dto.LabelPair;
import com.moyorak.api.review.dto.ReviewWithUserProjection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReviewTimeLabels {

    private final Map<Long, LabelPair> idToLabels;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    private ReviewTimeLabels(Map<Long, LabelPair> idToLabels) {
        this.idToLabels = idToLabels;
    }

    public static ReviewTimeLabels create(
            List<ReviewWithUserProjection> reviews, ReviewTimeRangeMapper timeMapper) {
        Map<Long, LabelPair> labelMap =
                reviews.stream()
                        .collect(
                                Collectors.toUnmodifiableMap(
                                        ReviewWithUserProjection::id,
                                        r ->
                                                new LabelPair(
                                                        timeMapper.mapServingTime(r.servingTime()),
                                                        timeMapper.mapWaitingTime(
                                                                r.waitingTime()))));

        return new ReviewTimeLabels(labelMap);
    }

    public static ReviewTimeLabels createFromUserReview(
            List<ReviewWithUserAndTeamRestaurantProjection> reviews,
            ReviewTimeRangeMapper timeMapper) {
        Map<Long, LabelPair> labelMap =
                reviews.stream()
                        .collect(
                                Collectors.toUnmodifiableMap(
                                        ReviewWithUserAndTeamRestaurantProjection::id,
                                        r ->
                                                new LabelPair(
                                                        timeMapper.mapServingTime(r.servingTime()),
                                                        timeMapper.mapWaitingTime(
                                                                r.waitingTime()))));

        return new ReviewTimeLabels(labelMap);
    }

    public String getServingLabel(Long reviewId) {
        return idToLabels.getOrDefault(reviewId, LabelPair.EMPTY).servingLabel();
    }

    public String getWaitingLabel(Long reviewId) {
        return idToLabels.getOrDefault(reviewId, LabelPair.EMPTY).waitingLabel();
    }

    public String convertDate(LocalDate date) {
        return formatter.format(date);
    }
}
