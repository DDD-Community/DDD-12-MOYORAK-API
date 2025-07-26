package com.moyorak.api.team.controller;

import com.moyorak.api.auth.domain.UserPrincipal;
import com.moyorak.api.team.service.TeamJoinFacade;
import com.moyorak.api.team.service.TeamUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    @DeleteMapping("/teams/{teamId}/team-members/me")
    @Operation(summary = "팀 탈퇴", description = "팀을 탈퇴합니다.")
    public void withdraw(
            @PathVariable @Positive final Long teamId,
            @AuthenticationPrincipal final UserPrincipal userPrincipal) {
        teamUserService.withdraw(userPrincipal.getId(), teamId);
    }

    @PostMapping("/teams/{teamId}/team-members")
    @Operation(summary = "팀 가입 신청", description = "팀 가입을 신청 합니다.")
    public void requestJoin(
            @PathVariable @Positive final Long teamId,
            @AuthenticationPrincipal final UserPrincipal userPrincipal) {
        teamJoinFacade.requestJoin(userPrincipal.getId(), teamId);
    }

    @PutMapping("/teams/{teamId}/team-members/{teamMemberId}/approve")
    @Operation(summary = "팀 가입 신청", description = "팀 가입을 신청 합니다.")
    public void approveRequestJoin(
            @PathVariable @Positive final Long teamId,
            @PathVariable @Positive final Long teamMemberId,
            @AuthenticationPrincipal final UserPrincipal userPrincipal) {
        teamUserService.approveRequestJoin(userPrincipal.getId(), teamId, teamMemberId);
    }
}
