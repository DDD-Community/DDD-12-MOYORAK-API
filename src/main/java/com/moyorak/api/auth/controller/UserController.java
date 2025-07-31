package com.moyorak.api.auth.controller;

import com.moyorak.api.auth.dto.SignUpRequest;
import com.moyorak.api.auth.dto.SignUpResponse;
import com.moyorak.api.auth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "[회원] 회원 관리 API", description = "회원에 관한 정보를 관리합니다.")
class UserController {

    private final UserService userService;

    @PostMapping("/sign-up")
    @Operation(summary = "[회원] 회원 가입", description = "회원 가입을 요청합니다.")
    public SignUpResponse signUp(@RequestBody @Valid final SignUpRequest request) {
        return userService.signUp(request);
    }
}
