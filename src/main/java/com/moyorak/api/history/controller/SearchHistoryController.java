package com.moyorak.api.history.controller;

import com.moyorak.api.auth.domain.UserPrincipal;
import com.moyorak.api.history.dto.SearchHistoryListResponse;
import com.moyorak.api.history.dto.SearchHistoryRequest;
import com.moyorak.api.history.service.SearchHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
@Tag(name = "[기록] 팀 맛집 검색 기록 API", description = "팀 맛집 검색 관리를 위한 API 입니다.")
class SearchHistoryController {

    private final SearchHistoryService searchHistoryService;

    @GetMapping("/teams/{teamId}/team-members/me/search-history")
    @Operation(summary = "자신의 팀 맛집 검색 기록 조회", description = "자신의 팀 맛집 검색 기록을 조회합니다.")
    public SearchHistoryListResponse getSearchHistories(
            @PathVariable @Positive final Long teamId,
            @AuthenticationPrincipal final UserPrincipal userPrincipal) {
        return searchHistoryService.getSearchHistories(
                SearchHistoryRequest.create(userPrincipal.getId(), teamId));
    }

    @DeleteMapping("/teams/{teamId}/team-members/me/search-history/{searchHistoryId}")
    @Operation(summary = "자신의 팀 맛집 검색 기록 삭제", description = "자신의 팀 맛집 검색 기록을 삭제합니다.")
    public void deleteSearchHistory(
            @PathVariable @Positive final Long searchHistoryId,
            @AuthenticationPrincipal final UserPrincipal userPrincipal) {
        searchHistoryService.deleteSearchHistory(searchHistoryId, userPrincipal.getId());
    }
}
