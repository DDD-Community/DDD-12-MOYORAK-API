package com.moyorak.api.team.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "[팀] 팀 생성 응답 DTO")
public record TeamCreateResponse(@Schema(description = "팀 ID", example = "1") Long teamId) {
    public static TeamCreateResponse of(Long teamId) {
        return new TeamCreateResponse(teamId);
    }
}
