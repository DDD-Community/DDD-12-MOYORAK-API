package com.moyorak.api.company.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import org.springdoc.core.annotations.ParameterObject;

@ParameterObject
@Schema(title = "회사 이름 검색 요청 DTO")
public record CompanySearchRequest(
        @Schema(description = "회사 고유 ID", example = "21") Long companyId,
        @Schema(description = "회사 이름", example = "Backend") String name) {

    @AssertTrue(message = "회사 ID와 회사 이름 중 최소 하나는 필수입니다.")
    public boolean hasAtLeastOneCondition() {
        return companyId != null || (name != null && !name.trim().isEmpty());
    }
}
