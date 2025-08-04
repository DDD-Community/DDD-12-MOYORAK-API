package com.moyorak.api.team.controller;

import com.moyorak.api.auth.domain.UserPrincipal;
import com.moyorak.api.team.dto.TeamUserListRequest;
import com.moyorak.api.team.dto.TeamUserResponse;
import com.moyorak.api.team.dto.TeamUserRoleUpdateRequest;
import com.moyorak.api.team.service.TeamJoinFacade;
import com.moyorak.api.team.service.TeamUserManagementService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    private final TeamJoinFacade teamJoinFacade;
    private final TeamUserManagementService teamUserManagementService;

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
        return teamUserManagementService.getTeamUsers(userPrincipal.getId(), teamId, request);
    }

    @PostMapping("/teams/{teamId}/team-members")
    @Operation(summary = "팀 가입 신청", description = "팀 가입을 신청 합니다.")
    public void requestJoin(
            @PathVariable @Positive final Long teamId,
            @AuthenticationPrincipal final UserPrincipal userPrincipal) {
        teamJoinFacade.requestJoin(userPrincipal.getId(), teamId);
    }

    @PutMapping("/teams/{teamId}/team-members/{teamMemberId}/approve")
    @Operation(summary = "팀 가입 신청을 승인합니다.", description = "팀 가입 신청을 승인 합니다.")
    public void approveRequestJoin(
            @PathVariable @Positive final Long teamId,
            @PathVariable @Positive final Long teamMemberId,
            @AuthenticationPrincipal final UserPrincipal userPrincipal) {
        teamUserManagementService.approveRequestJoin(userPrincipal.getId(), teamId, teamMemberId);
    }

    @PutMapping("/teams/{teamId}/team-members/{teamMemberId}/reject")
    @Operation(summary = "팀 가입 신청을 거절합니다.", description = "팀 가입 신청을 거절 합니다.")
    public void rejectRequestJoin(
            @PathVariable @Positive final Long teamId,
            @PathVariable @Positive final Long teamMemberId,
            @AuthenticationPrincipal final UserPrincipal userPrincipal) {
        teamUserManagementService.rejectRequestJoin(userPrincipal.getId(), teamId, teamMemberId);
    }

    @PutMapping("/teams/{teamId}/team-members/{teamMemberId}/role")
    @Operation(summary = "팀 멤버 역할 변경", description = "팀 멤버 역할을 변경합니다.")
    public void updateRole(
            @PathVariable @Positive final Long teamId,
            @PathVariable @Positive final Long teamMemberId,
            @AuthenticationPrincipal final UserPrincipal userPrincipal,
            @Valid @RequestBody final TeamUserRoleUpdateRequest request) {
        teamUserManagementService.updateRole(
                userPrincipal.getId(), teamId, teamMemberId, request.role());
    }

    @DeleteMapping("/teams/{teamId}/team-members/{teamMemberId}")
    @Operation(summary = "팀 멤버 강퇴", description = "팀 멤버를 강퇴합니다.")
    public void expel(
            @PathVariable @Positive final Long teamId,
            @PathVariable @Positive final Long teamMemberId,
            @AuthenticationPrincipal final UserPrincipal userPrincipal) {
        teamUserManagementService.expel(userPrincipal.getId(), teamId, teamMemberId);
    }
}
