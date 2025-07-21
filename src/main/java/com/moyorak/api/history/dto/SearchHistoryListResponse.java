package com.moyorak.api.history.dto;

import com.moyorak.api.history.domain.SearchHistory;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(title = "팀 맛집 검색 기록 응답 DTO")
public record SearchHistoryListResponse(
        @ArraySchema(
                        schema =
                                @Schema(
                                        description = "검색 기록",
                                        implementation = SearchHistoryResponse.class),
                        arraySchema = @Schema(description = "검색 기록 응답 리스트"))
                List<SearchHistoryResponse> searchHistories) {
    public static SearchHistoryListResponse from(final List<SearchHistory> searchHistories) {
        return new SearchHistoryListResponse(
                searchHistories.stream().map(SearchHistoryResponse::from).toList());
    }
}
