package com.moyorak.api.history.dto;

import com.moyorak.api.history.domain.SearchHistory;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(title = "팀 맛집 검색 기록 응답 DTO")
public record SearchHistoryResponse(
        @Schema(description = "검색 기록 고유 ID", example = "1") Long id,
        @Schema(description = "검색 키워드", example = "우가우가 국밥") String keyword,
        @Schema(description = "검색 날짜", example = "2025-07-21T20:19:29") LocalDateTime createdDate) {
    public static SearchHistoryResponse from(final SearchHistory searchHistory) {
        return new SearchHistoryResponse(
                searchHistory.getId(), searchHistory.getKeyword(), searchHistory.getCreatedDate());
    }
}
