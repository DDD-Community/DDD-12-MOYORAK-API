package com.moyorak.api.company.dto;

import com.moyorak.api.company.domain.CompanySearch;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.stream.Collectors;
import org.springdoc.core.annotations.ParameterObject;

@ParameterObject
@Schema(title = "회사 이름 검색 응답 DTO")
public record CompanySearchListResponse(
        @ArraySchema(
                        schema =
                                @Schema(
                                        description = "회사 정보",
                                        implementation = CompanySearchResponse.class),
                        arraySchema = @Schema(description = "회사 검색 응답 리스트"))
                List<CompanySearchResponse> searchResponses) {
    public static CompanySearchListResponse from(final List<CompanySearch> entities) {
        return new CompanySearchListResponse(
                entities.stream().map(CompanySearchResponse::from).collect(Collectors.toList()));
    }
}
