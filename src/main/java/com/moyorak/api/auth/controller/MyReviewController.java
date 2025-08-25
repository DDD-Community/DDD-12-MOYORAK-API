package com.moyorak.api.auth.controller;

import com.moyorak.api.auth.domain.UserPrincipal;
import com.moyorak.api.auth.dto.UserReviewRequest;
import com.moyorak.api.auth.dto.UserReviewResponse;
import com.moyorak.api.auth.service.UserReviewFacade;
import com.moyorak.global.domain.ListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
@Tag(name = "[회원] [마이] 마이페이지 API", description = "마이페이지 관련 API 입니다.")
class MyReviewController {

    private final UserReviewFacade userReviewFacade;

    @GetMapping("/reviews")
    @Operation(summary = "[리뷰] 사용자 리뷰 조회", description = "사용자 리뷰를 조회합니다.")
    public ListResponse<UserReviewResponse> getUserReviews(
            @AuthenticationPrincipal final UserPrincipal userPrincipal,
            @Valid final UserReviewRequest request) {
        return userReviewFacade.getReviewWithUserByUserId(userPrincipal.getId(), request);
    }
}
