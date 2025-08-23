package com.moyorak.api.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "[회원] 회원 소속 정보 응답 DTO")
public record UserOrganisationResponse(
        @Schema(description = "회사 고유 ID", example = "1") Long companyId,
        @Schema(description = "팀 고유 ID", example = "1") Long teamId) {

    public static UserOrganisationResponse from(final Long companyId, final Long teamId) {
        return new UserOrganisationResponse(companyId, teamId);
    }
}
