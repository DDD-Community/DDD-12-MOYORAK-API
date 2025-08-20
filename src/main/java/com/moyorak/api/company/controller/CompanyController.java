package com.moyorak.api.company.controller;

import com.moyorak.api.company.dto.CompanyPositionResponse;
import com.moyorak.api.company.dto.CompanySaveRequest;
import com.moyorak.api.company.dto.CompanySaveResponse;
import com.moyorak.api.company.dto.CompanySearchListResponse;
import com.moyorak.api.company.dto.CompanySearchRequest;
import com.moyorak.api.company.service.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
@Tag(name = "[회사] 회사 API", description = "회사를 위한 API 입니다.")
class CompanyController {

    private final CompanyService companyService;

    @PostMapping
    @Operation(summary = "회사 저장", description = "회사를 저장 합니다.")
    public CompanySaveResponse save(@Valid @RequestBody final CompanySaveRequest saveRequest) {
        return companyService.save(saveRequest);
    }

    @GetMapping
    @Operation(summary = "회사 검색", description = "회사를 검색 합니다.")
    public CompanySearchListResponse searchTeamsInfo(@Valid final CompanySearchRequest request) {
        return companyService.search(request);
    }

    @GetMapping("/{companyId}/position")
    @Operation(summary = "회사 위치(경도, 위도) 조회", description = "회사 위치를 조회합니다.")
    public CompanyPositionResponse getCompanyPosition(@PathVariable final Long companyId) {
        return companyService.getCompanyPosition(companyId);
    }
}
