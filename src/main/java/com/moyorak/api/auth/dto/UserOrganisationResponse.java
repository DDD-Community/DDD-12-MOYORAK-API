package com.moyorak.api.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "[회원] 회원 소속 정보 응답 DTO")
public record UserOrganisationResponse(
        @Schema(description = "회사 고유 ID", example = "1") Long companyId,
        @Schema(description = "팀 고유 ID", example = "1") Long teamId,
        @Schema(description = "팀 역할", example = "팀원") String teamRole) {

    public static UserOrganisationResponse from(
            final Long companyId, final Long teamId, final String teamRole) {
        return new UserOrganisationResponse(companyId, teamId, teamRole);
    }
}
