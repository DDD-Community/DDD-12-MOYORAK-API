package com.moyorak.api.company.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "[회사] 회사 저장 응답 DTO")
public record CompanySaveResponse(
        @Schema(description = "회사 고유 ID", example = "13") Long companyId) {

    public static CompanySaveResponse from(final Long companyId) {
        return new CompanySaveResponse(companyId);
    }
}
