package com.moyorak.api.team.controller;

import com.moyorak.api.team.dto.TeamSearchListResponse;
import com.moyorak.api.team.dto.TeamSearchRequest;
import com.moyorak.api.team.service.TeamService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
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
@Tag(name = "[회사] [팀] 팀 관리 API", description = "팀 관리를 위한 API 입니다.")
class TeamController {

    private final TeamService teamService;

    @GetMapping("/companies/{companyId}")
    public TeamSearchListResponse searchTeamsInfo(
            @PathVariable @Positive final Long companyId, @Valid final TeamSearchRequest request) {
        return teamService.search(companyId, request);
    }
}
