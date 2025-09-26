package com.moyorak.api.team.controller;

import com.moyorak.api.auth.domain.UserPrincipal;
import com.moyorak.api.team.dto.TeamCreateResponse;
import com.moyorak.api.team.dto.TeamSaveRequest;
import com.moyorak.api.team.dto.TeamSearchListResponse;
import com.moyorak.api.team.dto.TeamSearchRequest;
import com.moyorak.api.team.service.TeamFacade;
import com.moyorak.api.team.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
@Tag(name = "[회사] [팀] 팀 관리 API", description = "팀 관리를 위한 API 입니다.")
class TeamController {

    private final TeamFacade teamFacade;

    private final TeamService teamService;

    @GetMapping("/companies/{companyId}")
    @Operation(summary = "[팀] 팀 조회", description = "팀 정보를 조회합니다.")
    public TeamSearchListResponse searchTeamsInfo(
            @PathVariable @Positive final Long companyId, @Valid final TeamSearchRequest request) {
        return teamService.search(companyId, request);
    }

    @PostMapping("/companies/{companyId}/teams")
    @Operation(summary = "[팀] 팀 생성", description = "팀을 생성하고, 최초 생성자는 팀장이 됩니다.")
    public TeamCreateResponse createTeam(
            @PathVariable @Positive final Long companyId,
            @AuthenticationPrincipal final UserPrincipal userPrincipal,
            @RequestBody @Valid final TeamSaveRequest request) {
        return teamFacade.create(companyId, userPrincipal.getId(), request);
    }
}
