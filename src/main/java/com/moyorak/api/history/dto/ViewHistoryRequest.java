package com.moyorak.api.history.dto;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public record ViewHistoryRequest(Long userId, Long teamId) {
    private static final int FIXED_PAGE_NUMBER = 0;
    private static final int FIXED_PAGE_SIZE = 5;

    public static ViewHistoryRequest create(final Long userId, final Long teamId) {
        return new ViewHistoryRequest(userId, teamId);
    }

    public Pageable toRecentPageable() {
        return PageRequest.of(
                FIXED_PAGE_NUMBER, FIXED_PAGE_SIZE, Sort.by(Sort.Direction.DESC, "id"));
    }
}
