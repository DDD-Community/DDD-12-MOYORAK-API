package com.moyorak.api.auth.controller;

import com.moyorak.api.auth.domain.UserPrincipal;
import com.moyorak.api.auth.dto.MealTagSaveRequest;
import com.moyorak.api.auth.service.MealTagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
@Tag(name = "[회원] [마이] 마이페이지 API", description = "마이페이지 관련 API 입니다.")
class MealTagController {

    private final MealTagService mealTagService;

    @PutMapping("/meal/tags")
    @Operation(summary = "[알러지, 비선호 음식] 알러지, 비선호 음식 등록", description = "알러지, 비선호 음식 리스트를 저장합니다.")
    public void foodFlagRegister(
            @AuthenticationPrincipal final UserPrincipal userPrincipal,
            @RequestBody @Valid final MealTagSaveRequest request) {
        mealTagService.foodFlagRegister(userPrincipal.getId(), request);
    }
}
