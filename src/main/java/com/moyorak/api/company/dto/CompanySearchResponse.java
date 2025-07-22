package com.moyorak.api.company.dto;

import com.moyorak.api.company.domain.CompanySearch;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springdoc.core.annotations.ParameterObject;

@ParameterObject
@Schema(title = "회사 이름 검색 응답 DTO")
public record CompanySearchResponse(
        @Schema(description = "회사 고유 ID", example = "21") Long companyId,
        @Schema(description = "회사 이름", example = "Backend파트") String name) {

    public static CompanySearchResponse from(final CompanySearch companySearch) {
        return new CompanySearchResponse(companySearch.getId(), companySearch.getName());
    }
}
