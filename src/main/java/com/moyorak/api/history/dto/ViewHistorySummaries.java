package com.moyorak.api.history.dto;

import com.moyorak.api.history.domain.ViewHistory;
import java.util.List;

public record ViewHistorySummaries(List<ViewHistorySummary> summaries) {
    public static ViewHistorySummaries from(final List<ViewHistory> viewHistories) {
        return new ViewHistorySummaries(
                viewHistories.stream().map(ViewHistorySummary::from).toList());
    }

    public List<Long> getTeamRestaurantIds() {
        return summaries.stream().map(ViewHistorySummary::teamRestaurantId).toList();
    }
}
