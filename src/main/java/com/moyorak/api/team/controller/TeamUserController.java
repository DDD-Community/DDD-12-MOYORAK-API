package com.moyorak.api.team.controller;

import com.moyorak.api.auth.domain.UserPrincipal;
import com.moyorak.api.team.dto.TeamUserListRequest;
import com.moyorak.api.team.dto.TeamUserResponse;
import com.moyorak.api.team.service.TeamUserService;
import com.moyorak.global.domain.ListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@Tag(name = "[팀 멤버] 팀 멤버 API", description = "팀 멤버 관련 API입니다.")
class TeamUserController {

    private final TeamUserService teamUserService;

    @DeleteMapping("/teams/{teamId}/team-members/me")
    @Operation(summary = "팀 탈퇴", description = "팀을 탈퇴합니다.")
    public void withdraw(
            @PathVariable @Positive final Long teamId,
            @AuthenticationPrincipal final UserPrincipal userPrincipal) {
        teamUserService.withdraw(userPrincipal.getId(), teamId);
    }

    @GetMapping("/teams/{teamId}/team-members")
    @Operation(summary = "팀 멤버 리스트 조회", description = "팀 멤버 리스트를 조회합니다.")
    public ListResponse<TeamUserResponse> getTeamUsers(
            @PathVariable @Positive final Long teamId,
            @AuthenticationPrincipal final UserPrincipal userPrincipal,
            @Valid final TeamUserListRequest request) {
        return teamUserService.getTeamUsers(userPrincipal.getId(), teamId, request);
    }
}
