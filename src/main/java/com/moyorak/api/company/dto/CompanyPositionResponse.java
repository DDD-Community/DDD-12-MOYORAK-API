package com.moyorak.api.company.dto;

import com.moyorak.api.company.domain.Company;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "[회사] 회사 위치 응답 DTO")
public record CompanyPositionResponse(
        @Schema(description = "경도", example = "126.12345") double longitude,
        @Schema(description = "위도", example = "37.12345") double latitude) {
    public static CompanyPositionResponse from(final Company company) {
        return new CompanyPositionResponse(company.getLongitude(), company.getLatitude());
    }
}
