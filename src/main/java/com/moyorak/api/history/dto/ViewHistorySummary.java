package com.moyorak.api.history.dto;

import com.moyorak.api.history.domain.ViewHistory;

public record ViewHistorySummary(Long id, Long teamRestaurantId) {
    public static ViewHistorySummary from(final ViewHistory viewHistory) {
        return new ViewHistorySummary(viewHistory.getId(), viewHistory.getTeamRestaurantId());
    }
}
