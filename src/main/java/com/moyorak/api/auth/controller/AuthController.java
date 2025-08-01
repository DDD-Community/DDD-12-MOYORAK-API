package com.moyorak.api.auth.controller;

import com.moyorak.api.auth.domain.UserPrincipal;
import com.moyorak.api.auth.dto.RefreshResponse;
import com.moyorak.api.auth.dto.SignInRequest;
import com.moyorak.api.auth.dto.SignInResponse;
import com.moyorak.api.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "[회원] [토큰] 토큰 관리 API", description = "토큰 관리를 위한 API 입니다.")
class AuthController {

    private final AuthService authService;

    @PostMapping("/sign-in")
    @Operation(summary = "[로그인] 토큰 발급", description = "로그인을 하여 토큰을 발급 받습니다.")
    public SignInResponse generateToken(@RequestBody final SignInRequest request) {
        return authService.generate(request.userId());
    }

    @SecurityRequirement(name = "JWT")
    @PostMapping("/sign-out")
    @Operation(summary = "[로그아웃] 토큰 초기화", description = "저장 된 토큰 정보를 초기화합니다.")
    public void signOut(@AuthenticationPrincipal final UserPrincipal userPrincipal) {
        authService.signOut(userPrincipal.getId());
    }

    @PostMapping("/refresh")
    @Operation(summary = "[토큰 갱신] 토큰 갱신", description = "액세스 토큰을 재발급 합니다.")
    public RefreshResponse refresh(
            @Parameter(description = "리프레시 토큰", required = true)
                    @RequestHeader(name = "X-REFRESH-TOKEN")
                    @NotBlank
                    final String refreshToken) {
        return authService.refresh(refreshToken);
    }
}
