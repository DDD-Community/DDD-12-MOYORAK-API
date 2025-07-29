package com.moyorak.api.team.dto;

import com.moyorak.api.team.domain.TeamRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(title = "[팀 멤버] 팀 멤버 역할 변경 요청 DTO")
public record TeamUserRoleUpdateRequest(
        @NotNull(message = "변경할 역할을 입력해주세요.")
                @Schema(description = "변경할 팀 역할", example = "TEAM_ADMIN")
                TeamRole role) {}
