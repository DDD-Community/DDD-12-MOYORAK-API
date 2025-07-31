package com.moyorak.api.review.domain;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

public class ReviewTimeRangeMapper {
    private final List<ReviewServingTime> servingRanges;
    private final List<ReviewWaitingTime> waitingRanges;

    public ReviewTimeRangeMapper(
            List<ReviewServingTime> servingRanges, List<ReviewWaitingTime> waitingRanges) {
        this.servingRanges =
                servingRanges.stream()
                        .filter(ReviewServingTime::isUse)
                        .sorted(Comparator.comparingInt(ReviewServingTime::getServingTimeValue))
                        .toList();

        this.waitingRanges =
                waitingRanges.stream()
                        .filter(ReviewWaitingTime::isUse)
                        .sorted(Comparator.comparingInt(ReviewWaitingTime::getWaitingTimeValue))
                        .toList();
    }

    public static ReviewTimeRangeMapper create(
            List<ReviewServingTime> servingRanges, List<ReviewWaitingTime> waitingRanges) {
        // 유효한 값만 정렬해서 세팅
        List<ReviewServingTime> sortedServing =
                servingRanges.stream()
                        .filter(ReviewServingTime::isUse)
                        .sorted(Comparator.comparingInt(ReviewServingTime::getServingTimeValue))
                        .toList();

        List<ReviewWaitingTime> sortedWaiting =
                waitingRanges.stream()
                        .filter(ReviewWaitingTime::isUse)
                        .sorted(Comparator.comparingInt(ReviewWaitingTime::getWaitingTimeValue))
                        .toList();

        return new ReviewTimeRangeMapper(sortedServing, sortedWaiting);
    }

    public String mapServingTime(int input) {
        return findClosestRange(
                servingRanges,
                input,
                ReviewServingTime::getServingTimeValue,
                ReviewServingTime::getServingTime);
    }

    public String mapWaitingTime(int input) {
        return findClosestRange(
                waitingRanges,
                input,
                ReviewWaitingTime::getWaitingTimeValue,
                ReviewWaitingTime::getWaitingTime);
    }

    private <T> String findClosestRange(
            List<T> ranges,
            int input,
            Function<T, Integer> valueExtractor,
            Function<T, String> labelExtractor) {
        T result = null;
        for (T range : ranges) {
            if (input < valueExtractor.apply(range)) break;
            result = range;
        }
        return result != null ? labelExtractor.apply(result) : "기타";
    }
}
