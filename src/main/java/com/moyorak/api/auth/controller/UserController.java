package com.moyorak.api.auth.controller;

import com.moyorak.api.auth.domain.UserPrincipal;
import com.moyorak.api.auth.dto.SignUpRequest;
import com.moyorak.api.auth.dto.SignUpResponse;
import com.moyorak.api.auth.service.UserFacade;
import com.moyorak.api.auth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "[회원] 회원 관리 API", description = "회원에 관한 정보를 관리합니다.")
class UserController {

    private final UserFacade userFacade;

    private final UserService userService;

    @PostMapping("/sign-up")
    @Operation(summary = "[회원] 회원 가입", description = "회원 가입을 요청합니다.")
    public SignUpResponse signUp(@RequestBody @Valid final SignUpRequest request) {
        return userService.signUp(request);
    }

    @SecurityRequirement(name = "JWT")
    @DeleteMapping("/unregister")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "회원 탈퇴 성공"),
                @ApiResponse(
                        responseCode = "400",
                        description =
                                "<ol><li>팀에 관리자로 속해 있는 경우</li><li>회원 정보가 없는 경우</li><li>로그인 정보가 없는 경우</li><li>예상치 못한 클라이언트 오류</li></ol>"),
                @ApiResponse(responseCode = "500", description = "예상치 못한 서버 오류"),
            })
    @Operation(
            summary = "[회원] 회원 탈퇴",
            description = "회원 탈퇴를 요청합니다.\n이름과 생년월일, 성별, 알러지 + 비선호 음식, 회사, 팀 정보가 제거 됩니다.")
    public void unregister(@AuthenticationPrincipal final UserPrincipal userPrincipal) {
        userFacade.unregister(userPrincipal.getId());
    }
}
