package com.moyorak.api.history.controller;

import com.moyorak.api.auth.domain.UserPrincipal;
import com.moyorak.api.history.dto.ViewHistoryListResponse;
import com.moyorak.api.history.dto.ViewHistoryRequest;
import com.moyorak.api.history.service.ViewHistoryFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
@Tag(name = "[기록] 팀 맛집 상세 조회 기록 API", description = "팀 맛집 상세 조회 기록 관리를 위한 API 입니다.")
class ViewHistoryController {

    private final ViewHistoryFacade viewHistoryFacade;

    @GetMapping("/teams/{teamId}/team-members/me/view-history")
    @Operation(summary = "자신의 팀 맛집 상세 조회 기록", description = "자신의 팀 맛집 상세 조회 기록을 조회합니다.")
    public ViewHistoryListResponse getViewHistories(
            @PathVariable @Positive final Long teamId,
            @AuthenticationPrincipal final UserPrincipal userPrincipal) {
        return viewHistoryFacade.getViewHistories(
                ViewHistoryRequest.create(userPrincipal.getId(), teamId));
    }
}
